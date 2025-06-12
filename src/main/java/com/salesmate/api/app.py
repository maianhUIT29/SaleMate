from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional, Dict
from pathlib import Path
import pandas as pd
import numpy as np
import joblib, pickle
from statsmodels.tsa.statespace.sarimax import SARIMAXResults
from datetime import datetime, timedelta

# ──────────────────────────────────────────────────────────
# Config
# ──────────────────────────────────────────────────────────
MODELS_DIR = Path(__file__).parent / "saved_models"
FORECAST_HORIZON = 90  # mặc định dự báo 90 ngày tới

app = FastAPI(
    title="Stock-Out Forecasting API",
    description="Predict quantity sold and out-of-stock date for each product",
    version="1.0.0",
)

# ──────────────────────────────────────────────────────────
# Pydantic schema
# ──────────────────────────────────────────────────────────
class PredictRequest(BaseModel):
    product_id: int
    current_stock: Optional[int] = None   # nếu muốn tính ngày hết kho
    horizon: Optional[int] = FORECAST_HORIZON

class PredictResponse(BaseModel):
    product_id: int
    horizon: int
    daily_forecast: Dict[str, float]      # {YYYY-MM-DD: predicted_qty}
    predicted_out_date: Optional[str]     # YYYY-MM-DD hoặc null
    model_name: str

# ──────────────────────────────────────────────────────────
# Helper: load model theo product_id
# ──────────────────────────────────────────────────────────
def load_model(product_id: int):
    # Ưu tiên Prophet → Holt-Winters (joblib) → Sarima/Arima (pkl)
    for pattern in [
        f"prophet_{product_id}.pkl",
        f"holtwin_{product_id}.joblib",
        f"arima011_{product_id}.pkl",
        f"sarima_{product_id}.pkl",
    ]:
        fpath = MODELS_DIR / pattern
        if fpath.exists():
            if pattern.endswith(".joblib"):
                model = joblib.load(fpath)
            else:
                # SARIMAXResults và Prophet đều pickle
                with open(fpath, "rb") as f:
                    model = pickle.load(f)
            return pattern.split("_")[0], model
    
    # Không tìm thấy model nào, trả về mô hình rule-based
    print(f"Không tìm thấy model cho sản phẩm {product_id}, sử dụng rule-based model")
    return "rule-based", None

# ──────────────────────────────────────────────────────────
# Helper: sinh forecast N ngày với từng loại model
# ──────────────────────────────────────────────────────────
def forecast_next(model_name: str, model, steps: int, product_id: int = None):
    if model_name == "rule-based":
        today = datetime.now().date()
        dates = pd.date_range(start=today + timedelta(days=1), periods=steps, freq='D')
        return pd.Series([1.0] * steps, index=dates)
    
    try:
        if model_name in {"sarima", "arima011"}:
            pred = model.get_forecast(steps=steps).predicted_mean
            return pred
        elif model_name == "holtwin":
            pred = model.forecast(steps)
            return pd.Series(pred.values, index=pd.date_range(
                start=datetime.today().date() + pd.Timedelta(days=1),
                periods=steps, freq="D"))
        elif model_name == "prophet":
            future = model.make_future_dataframe(periods=steps, freq="D")
            fc = model.predict(future)
            return fc.set_index("ds")["yhat"].iloc[-steps:]
        else:
            print(f"Unknown model type: {model_name}, using rule-based forecast")
            # Fallback to rule-based
            today = datetime.now().date()
            dates = pd.date_range(start=today + timedelta(days=1), periods=steps, freq='D')
            return pd.Series([1.0] * steps, index=dates)
    except Exception as e:
        print(f"Error forecasting with {model_name} model: {str(e)}")
        # Fallback to rule-based on exception
        today = datetime.now().date()
        dates = pd.date_range(start=today + timedelta(days=1), periods=steps, freq='D')
        return pd.Series([1.0] * steps, index=dates)

# ──────────────────────────────────────────────────────────
# Route: /predict
# ──────────────────────────────────────────────────────────
@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    # 1) Load model
    model_name, model = load_model(req.product_id)
    
    # 2) Forecast
    horizon = req.horizon or FORECAST_HORIZON
    pred_series = forecast_next(model_name, model, horizon, req.product_id)

    # 3) Tính ngày hết hàng nếu có current_stock
    out_date = None
    if req.current_stock is not None:
        try:
            remaining = req.current_stock
            for date, qty in pred_series.items():
                try:
                    # Đảm bảo qty là số
                    qty_value = float(qty) if qty is not None else 0.0
                    remaining -= max(qty_value, 0)  # bán âm coi như 0
                    if remaining <= 0:
                        out_date = date.strftime("%Y-%m-%d")
                        break
                except (TypeError, ValueError) as e:
                    print(f"Error processing quantity for date {date}: {str(e)}")
                    continue
        except Exception as e:
            print(f"Error calculating predicted out date: {str(e)}")
            # Nếu có lỗi, đặt predicted_out_date là None

    # 4) Build response
    try:
        # Xây dựng daily_forecast dict một cách an toàn
        forecast_dict = {}
        for d, v in pred_series.items():
            try:
                # Đảm bảo d có thể chuyển thành string và v có thể chuyển thành float
                if d is not None:
                    date_str = d.strftime("%Y-%m-%d")
                    float_val = float(v) if v is not None else 0.0
                    forecast_dict[date_str] = float_val
            except (ValueError, TypeError, AttributeError) as e:
                print(f"Error converting forecast value: {str(e)}")
                continue
        
        return PredictResponse(
            product_id=req.product_id,
            horizon=horizon,
            daily_forecast=forecast_dict,
            predicted_out_date=out_date,
            model_name=model_name,
        )
    except Exception as e:
        print(f"Error building response: {str(e)}")
        # Fallback response với daily_forecast trống
        return PredictResponse(
            product_id=req.product_id,
            horizon=horizon,
            daily_forecast={},
            predicted_out_date=None,
            model_name=model_name,
        )

# ──────────────────────────────────────────────────────────
# Healthcheck
# ──────────────────────────────────────────────────────────
@app.get("/")
def root():
    return {"msg": "Stock-Out Forecasting API. Go to /docs for Swagger UI."}
