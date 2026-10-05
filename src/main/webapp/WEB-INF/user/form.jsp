<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Thông tin User</title></head><body><h1>${empty user ? 'Thêm mới User' : 'Edit User'}</h1>
<form action="users" method="POST"><input type="hidden" name="action" value="${empty user ? 'insert' : 'update'}"><c:if test="${not empty user}"><input type="hidden" name="id" value="${user.id}"></c:if>
<p><label>Tên <input name="name" value="<c:out value='${user.name}'/>" maxlength="50" required></label></p>
<p><label>Email <input name="email" type="email" value="<c:out value='${user.email}'/>" maxlength="50" required></label></p>
<p><label>Quốc gia <input name="country" value="<c:out value='${user.country}'/>" maxlength="50" required></label></p>
<c:if test="${empty user}"><fieldset><legend>Quyền hạn (Permissions)</legend><label><input type="checkbox" name="permissions" value="1"> Thêm (Add)</label> <label><input type="checkbox" name="permissions" value="2"> Sửa (Edit)</label> <label><input type="checkbox" name="permissions" value="3"> Xóa (Delete)</label> <label><input type="checkbox" name="permissions" value="4"> Xem (View)</label></fieldset></c:if><button type="submit">Lưu</button></form><a href="users">Quay lại danh sách</a></body></html>
