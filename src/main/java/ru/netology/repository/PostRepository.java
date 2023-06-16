package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    private final ConcurrentHashMap<Long, String> repository = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public List<Post> all() {
        List<Post> allPosts = new ArrayList<>();
        if (repository.size() != 0) {
            repository.entrySet().stream().iterator().forEachRemaining(s -> allPosts.add(new Post(s.getKey(), s.getValue())));
        }
        return allPosts;
    }

    public Optional<Post> getById(long id) {
        String content = repository.get(id);
        return Optional.of(new Post(id, content));
    }

    public Post save(Post post) {
        long id = post.getId();
        String content = post.getContent();
        long newID = -1;
        if (id == 0) {
            do {  //проверка для ситуаций, если до этого посты шли с указанием ID и, соответственно, номера ID по порядку могут быть уже заняты
                newID = idCounter.incrementAndGet();
            } while (repository.containsKey(newID));

            repository.put(newID, content);
        } else {
            repository.put(id, content);
            newID = id;
        }
        return getById(newID).get();
    }

    public void removeById(long id) {
        repository.remove(id);
    }
}
