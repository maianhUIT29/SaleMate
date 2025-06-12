@echo off
echo ===============================================
echo   KHỞI ĐỘNG STOCK FORECAST API (PYTHON FASTAPI)
echo ===============================================
echo.
cd c:\Users\Nhan\Desktop\src\java\SalesMate\src\main\java\com\salesmate\api
echo Đang chạy API tại địa chỉ: http://localhost:8000
echo API Documentation: http://localhost:8000/docs
echo.
echo [Nhấn Ctrl+C để dừng API]
echo.
python -m uvicorn app:app --reload --port 8000