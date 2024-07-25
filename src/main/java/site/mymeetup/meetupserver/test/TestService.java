package site.mymeetup.meetupserver.test;

public interface TestService {

    public Object findAll();

    Object findOne(Long id);

    Object insert(Test test);

    Object delete(Long id);
}
