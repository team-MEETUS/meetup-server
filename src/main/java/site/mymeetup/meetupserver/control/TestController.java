package site.mymeetup.meetupserver.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.mymeetup.meetupserver.response.ApiResponse;
import site.mymeetup.meetupserver.test.Test;
import site.mymeetup.meetupserver.test.TestDto;
import site.mymeetup.meetupserver.test.TestService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/test")
    public ApiResponse<?> findAll() {
        return ApiResponse.success(testService.findAll());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/test/{testId}")
    public ApiResponse<?> findTest(@PathVariable("testId") Long id) {
        return ApiResponse.success(testService.findOne(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/test")
    public ApiResponse<?> insertOne(@RequestBody TestDto testDto) {
        Test test = new Test();
        test.setTestId(testDto.getTestId());
        test.setName(testDto.getName());
        return ApiResponse.success(testService.insert(test));
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/test/{testId}")
    public ApiResponse<?> delete(@PathVariable("testId") Long id) {
        return ApiResponse.success(testService.delete(id));
    }
}
