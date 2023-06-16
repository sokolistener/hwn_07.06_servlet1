package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    private static final String WEBPATH = "/api/posts";
    private static final String WEBPATHWID = WEBPATH+"/\\d+";
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("ru.netology");
    private PostController controller;

    @Override
    public void init() {
        controller = context.getBean("postController", PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            // primitive routing
            if (method.equals(GET) && path.equals(WEBPATH)) {
                controller.all(resp);
                return;
            }
            if (method.equals(GET) && path.matches(WEBPATHWID)) {
                // easy way
                final var id = parseId(path);
                System.out.println(id);
                controller.getById(id, resp);
                return;
            }
            if (method.equals(POST) && path.equals(WEBPATH)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals(DELETE) && path.matches(WEBPATHWID)) {
                // easy way
                final var id = parseId(path);
                controller.removeById(id, resp);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private static Long parseId(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}

