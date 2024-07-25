package site.mymeetup.meetupserver.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;

    @Override
    public Object findAll() {
        return testRepository.findAll();
    }

    @Override
    public Object findOne(Long id) {
        return testRepository.findById(id);
    }

    @Override
    public Object insert(Test test) {

        return testRepository.save(test);
    }

    @Override
    public Object delete(Long id) {
        testRepository.deleteById(id);
        return null;
    }
}
