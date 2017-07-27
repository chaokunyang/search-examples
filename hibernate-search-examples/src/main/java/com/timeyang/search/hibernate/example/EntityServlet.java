package com.timeyang.search.hibernate.example;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet(
        name = "entityServlet",
        urlPatterns = "/entities",
        loadOnStartup = 1
)
public class EntityServlet extends HttpServlet {
    private final Random random;
    private EntityManagerFactory factory;

    public EntityServlet() {
        try {
            this.random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.factory = Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa");

        // rebuild the indexes
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(factory.createEntityManager());
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.factory.close();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameter("query") != null && request.getParameter("query").length() > 0) {
            search(request, response);
            return;
        }

        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = this.factory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();

            CriteriaBuilder builder = manager.getCriteriaBuilder();

            CriteriaQuery<Author> q2 = builder.createQuery(Author.class);
            request.setAttribute("authors", manager.createQuery(
                    q2.select(q2.from(Author.class))
            ).getResultList());

            CriteriaQuery<Book> q3 = builder.createQuery(Book.class);
            request.setAttribute("books", manager.createQuery(
                    q3.select(q3.from(Book.class))
            ).getResultList());

            transaction.commit();

            request.getRequestDispatcher("/WEB-INF/jsp/view/entities.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            e.printStackTrace(response.getWriter());
        } finally {
            if (manager != null && manager.isOpen())
                manager.close();
        }
    }

    /**
     * http://localhost:8080/entities?query=Professional
     * @param request
     * @param response
     */
    private void search(HttpServletRequest request, HttpServletResponse response) {
        try {
            EntityManager em = factory.createEntityManager();
            FullTextEntityManager fullTextEntityManager =
                    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
            em.getTransaction().begin();

// create native Lucene query unsing the query DSL
// alternatively you can write the Lucene query using the Lucene query parser
// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
            QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder().forEntity(Book.class).get();
            org.apache.lucene.search.Query luceneQuery = qb
                    .keyword()
                    .onFields("title", "subtitle", "authors.name")
                    .matching(request.getParameter("query"))
                    .createQuery();

            // wrap Lucene query in a javax.persistence.Query
            javax.persistence.Query jpaQuery =
                    fullTextEntityManager.createFullTextQuery(luceneQuery, Book.class);

            // execute search
            List result = jpaQuery.getResultList();

            request.setAttribute("books", result);
            request.setAttribute("authors", new ArrayList<>());

            em.getTransaction().commit();

            request.getRequestDispatcher("/WEB-INF/jsp/view/entities.jsp")
                    .forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityManager manager = null;
        EntityTransaction transaction = null;
        try {
            manager = this.factory.createEntityManager();
            transaction = manager.getTransaction();
            transaction.begin();

            Author author = new Author();
            author.setName("Nicholas S. Williams");
            manager.persist(author);

            Book book = new Book();
            book.setTitle("Professional Java for Web Applications");
            manager.persist(book);

            transaction.commit();

            response.sendRedirect(request.getContextPath() + "/entities");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            e.printStackTrace(response.getWriter());
        } finally {
            if (manager != null && manager.isOpen())
                manager.close();
        }
    }
}
