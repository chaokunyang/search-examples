
<!DOCTYPE html>
<html>
    <head>
        <title>Entities</title>
    </head>
    <body>

        <b>Authors</b>
        <table>
            <tr>
                <th>ID</th>
                <th>Name</th>
            </tr>
            <%--@elvariable id="authors" type="java.util.List<com.timeyang.search.hibernate.example.Author>"--%>
            <c:forEach items="${authors}" var="a">
                <tr>
                    <td>${a.id}</td>
                    <td><c:out value="${a.name}" /></td>
                </tr>
            </c:forEach>
        </table><br />

        <b>Books</b>
        <table>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Subtitle</th>
                <th>PublicationDate</th>
            </tr>
            <%--@elvariable id="books" type="java.util.List<com.timeyang.search.hibernate.example.Book>"--%>
            <c:forEach items="${books}" var="b">
                <tr>
                    <td>${b.id}</td>
                    <td><c:out value="${b.title}" /></td>
                    <td><c:out value="${b.subtitle}" /></td>
                    <td><c:out value="${b.publicationDate}" /></td>
                </tr>
            </c:forEach>
        </table><br />

        <form method="post" action="<c:url value="/entities" />">
            <input type="submit" value="Add More Entities" />
        </form>
    </body>
</html>
