# Thực thi SQL không sử dụng Transaction
Java 17, Maven, MySQL, Tomcat 10.1. Tiếp tục ứng dụng WEB quản lý User.

## Nội dung
IUserDAO có insertUpdateWithoutTransaction(). UserDAO bật auto-commit, tạo lại bảng Employee, chèn Quynh (10.00) và Ngan (20.00), sau đó cố tình không gán tham số số 1 của UPDATE. SQLException được truyền về controller và ghi log. Hai INSERT vẫn tồn tại, UPDATE không thay đổi salary. Không gọi commit/rollback trong phương thức minh họa.

Dùng DECIMAL(15,2) theo phần mã DAO trong đề để giữ số tiền 999.99; không dùng INT(220). PreparedStatement được tạo sau khi tạo bảng để hỗ trợ cả driver chuẩn bị SQL ở phía server.

## Chạy
1. Dùng database demo dành riêng cho thực hành; chạy sql/setup.sql cho các chức năng User cũ. Phương thức minh họa tự tạo Employee.
2. Đặt DB_URL, DB_USER, DB_PASSWORD trước khi khởi động Tomcat. Mặc định MySQL localhost:3306/demo, user root, password trống. Không lưu mật khẩu thật trong repo.
3. mvn clean package; triển khai target/jdbc-without-transaction.war vào Tomcat 10.1.
4. Mở http://localhost:8080/jdbc-without-transaction/ rồi bấm Chạy bài minh họa; hoặc /users?action=test-without-tran theo đề.
5. Xem log SQLException (MySQL thường báo No value specified for parameter 1); chạy SELECT * FROM Employee; kết quả hai dòng Quynh 10.00, Ngan 20.00. Mỗi lần chạy sẽ xóa toàn bộ bảng Employee rồi tạo lại: chỉ dùng dữ liệu thực hành.

## Kiểm thử
mvn test chạy các kiểm thử JDBC với H2 ở chế độ MySQL. Bài test không dùng transaction chứng minh SQLException ở UPDATE và hai dòng INSERT vẫn tồn tại sau khi Connection đã đóng. Các kiểm thử transaction từ bài trước chứng minh sự khác biệt khi rollback.
H2 không thay thế chạy thực tế trên MySQL/Tomcat.

ACID: Atomicity (tất cả hoặc không); Consistency (giữ ràng buộc); Isolation (cách ly giao dịch); Durability (dữ liệu đã commit tồn tại bền vững). Auto-commit lưu từng statement riêng; không đảm bảo atomicity cho toàn bộ chuỗi ba statement.