package com.salesmate.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Cung cấp các câu chuyện cười liên quan đến các chủ đề khác nhau để
 * chatbot có thể sử dụng trong các câu trả lời
 */
public class JokeProvider {
    private static final Random random = new Random();
    private static final Map<String, String[]> jokesByTopic = new HashMap<>();
    
    static {
        // Khởi tạo các joke theo chủ đề
        
        // Sales/Shop jokes
        jokesByTopic.put("sales", new String[] {
            "Sếp hỏi nhân viên: 'Hôm nay bán được bao nhiêu?' Nhân viên đáp: 'Một cái.' Sếp: 'Thế thì không đủ!' Nhân viên: 'Nhưng đó là siêu thị của họ...'",
            "Khách hàng hỏi: 'Cái này còn bảo hành không?' Nhân viên: 'Dạ, bảo hành vĩnh viễn... cho đến khi nó hỏng!'",
            "Sao cửa hàng điện tử lại rất lạnh? Vì có quá nhiều... FANS!",
            "Hôm trước có khách vào cửa hàng than: 'Sao tất cả các sản phẩm đều đắt thế?' Tôi trả lời: 'Vì chúng đều được định giá bằng nỗi đau của sếp tôi!'",
            "Bạn biết điều gì xảy ra khi một cửa hàng laptop gặp cửa hàng điện thoại không? Chúng trở thành... đa nền tảng!"
        });
        
        // Tech jokes
        jokesByTopic.put("tech", new String[] {
            "Tại sao lập trình viên ghét đi bơi? Vì họ không muốn bị out of scopes!",
            "Tại sao JavaScript độc thân? Vì nó không thể cam kết (commit)!",
            "Bạn gọi 8 hobbits là gì? Một hobbyte!",
            "Có hai chuỗi đi vào một quán bar. Chuỗi đầu tiên hỏi: 'Địa điểm này có ổn không?' Chuỗi còn lại trả lời: 'Không biết, tôi nhận null pointer exception!'",
            "Tại sao lập trình viên đi làm luôn muộn? Vì họ gặp lỗi khi thức dậy... và phải debug giấc mơ!"
        });
        
        // Customer jokes
        jokesByTopic.put("customer", new String[] {
            "Khách hàng: 'Tôi muốn đổi sản phẩm này.' Nhân viên: 'Lý do là gì ạ?' Khách: 'Nó không vừa với kỳ vọng của tôi... mặc dù tôi mua sai size!'",
            "Khách hàng: 'Chiếc laptop này có chơi được game không?' Nhân viên: 'Dạ có, nhưng nó chỉ chơi được một lần thôi... sau đó sẽ chơi với sự kiên nhẫn của bạn!'",
            "Khách hàng VIP của chúng tôi giống thời tiết thế nào? Cả hai đều không thể đoán trước và luôn làm bạn phải dự đoán!",
            "Khách: 'Chiếc điện thoại này có chống nước không?' Nhân viên: 'Dạ có, nhưng nó không chống... khách hàng thử nghiệm tính năng này!'",
            "Khách hàng trung thành giống một mối tình đẹp: Hiếm, quý giá và thường không tồn tại khi gặp mức giá tốt hơn ở nơi khác!"
        });
        
        // HR/Staff jokes
        jokesByTopic.put("staff", new String[] {
            "Nhân viên IT giống siêu nhân ở điểm nào? Cả hai đều được gọi khi có thảm họa, nhưng chỉ một người được coi là anh hùng!",
            "Vì sao cuộc họp nhân sự lại giống pizza? Càng nhiều người tham gia, bạn càng nhận được ít phần!",
            "Sếp: 'Anh có thể làm việc dưới áp lực không?' Ứng viên: 'Tất nhiên, tôi được đào tạo về thợ lặn!'",
            "Đồng nghiệp: 'Sao anh luôn vui vẻ thế?' Tôi: 'Vì tôi không đọc email từ phòng nhân sự!'",
            "Nhân viên: 'Em có thể nghỉ phép không?' Sếp: 'Tại sao?' Nhân viên: 'Vì sếp cứ hỏi tại sao mỗi khi em xin nghỉ!'"
        });
        
        // General/Default jokes
        jokesByTopic.put("default", new String[] {
            "AI và người thường khác nhau thế nào? AI không bao giờ giả vờ làm việc khi sếp đi ngang qua!",
            "Tôi định kể một chuyện cười về Cloud Computing, nhưng tôi sợ nó sẽ bay mất!",
            "Tương lai thuộc về AI, còn hiện tại thuộc về... AI lỗi thời!",
            "Bạn có biết tại sao robot không bao giờ sợ hãi không? Vì chúng luôn có... nerves of steel!",
            "Cuộc sống như một database: Bạn nhận được kết quả dựa trên những gì bạn đưa vào!"
        });
    }
    
    /**
     * Lấy một câu joke ngẫu nhiên theo chủ đề
     * @param topic Chủ đề của joke
     * @param ownerName Tên chủ dùng để thay thế vào joke
     * @return Câu joke phù hợp
     */
    public static String getRandomJoke(String topic, String ownerName) {
        String[] jokes = jokesByTopic.getOrDefault(topic, jokesByTopic.get("default"));
        String joke = jokes[random.nextInt(jokes.length)];
        
        // Thay thế placeholder nếu có
        return joke.replace("{owner}", ownerName);
    }
    
    /**
     * Tạo một câu joke liên quan đến chủ đề input của người dùng
     * @param userInput Nội dung người dùng nhập vào
     * @param ownerName Tên chủ
     * @return Câu joke phù hợp
     */
    public static String getJokeForUserInput(String userInput, String ownerName) {
        userInput = userInput.toLowerCase();
        
        if (userInput.contains("bán") || userInput.contains("giá") || userInput.contains("cửa hàng") || 
            userInput.contains("sale") || userInput.contains("mua")) {
            return getRandomJoke("sales", ownerName);
        }
        
        if (userInput.contains("phần mềm") || userInput.contains("máy tính") || userInput.contains("laptop") || 
            userInput.contains("điện thoại") || userInput.contains("công nghệ") || userInput.contains("code")) {
            return getRandomJoke("tech", ownerName);
        }
        
        if (userInput.contains("khách") || userInput.contains("customer") || userInput.contains("mua hàng") || 
            userInput.contains("đơn hàng") || userInput.contains("dịch vụ")) {
            return getRandomJoke("customer", ownerName);
        }
        
        if (userInput.contains("nhân viên") || userInput.contains("nhân sự") || userInput.contains("nhân lực") || 
            userInput.contains("đồng nghiệp") || userInput.contains("tuyển dụng") || userInput.contains("staff")) {
            return getRandomJoke("staff", ownerName);
        }
        
        // Mặc định trả về joke ngẫu nhiên
        return getRandomJoke("default", ownerName);
    }
    
    /**
     * Tạo một joke đơn giản nhưng thú vị
     * @param ownerName Tên chủ để đưa vào joke
     * @return Câu joke ngẫu nhiên
     */
    public static String getSimpleJoke(String ownerName) {
        String[] simpleJokes = {
            ownerName + " có một app đếm cừu để dễ ngủ... mỗi tối đếm số bug trong ứng dụng!",
            "Tại sao AI không có bạn gái? Vì AI toàn nói chuyện với chính mình!",
            "Công nghệ giống " + ownerName + " ở điểm gì? Cả hai đều khiến bạn phấn khích ban đầu và tốn tiền về sau!",
            "Hai bit dữ liệu đang hẹn hò. Bit thứ nhất hỏi: 'Em có thấy mối quan hệ này phát triển không?' Bit thứ hai: 'Không, em nghĩ chúng ta chỉ nên làm... bạn nhị phân!'",
            "Ngày xưa có con chip tìm đến bác sĩ và nói: 'Bác sĩ ơi, tôi nghĩ mình bị virus!' Bác sĩ trả lời: 'Hãy uống thuốc chống virus và khởi động lại vào buổi sáng!'"
        };
        
        return simpleJokes[random.nextInt(simpleJokes.length)];
    }
}
