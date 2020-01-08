# Chapter3 테스트 더블

## 테스트 더블의 위력


## 테스트 더블의 종류

### Stubs (스텁)
_Stubs provide canned answers to calls made during the test, usually not responding at all to anything outside what's programmed in for the test.  
(Stub은 테스트 도중의 호출에 대해 미리 준비된 답변을 제공한다. 일반적으로 테스트 환경의 외부에는 전혀 응답하지 않는다.)_

> **Stub** (명사): 끝이 잘렸거나 유난히 짧은 것; 토막
- Stub의 목적은 원래의 구현을 **"최대한 단순한 것으로" 대체**하는 것.
- Stub은 실질적으로는 **아무 것도 하지 않는다**(혹은 그다지 의미 없는 **하드코딩**).
- 테스트 대상 객체(테스트 하고자 하는 기능)와 관련 없는 것들을 Stub으로 만들면 된다.
```java
public class LoggerStub implements Logger {
    public void log(LogLevel level, String message) {
        // 아무 일도 안한다.
    }

    public LogLevel getLogLevel() {
        return LogLevel.WARN;   // 하드코딩된 값을 반환한다.
    }
}

// 위 예제의 경우, 테스트 대상 코드는 로깅과는 전혀 관련이 없다.
// 또한 테스트 Suite가 콘솔에 로그를 쏟아내는 상황도 원하지 않는다.
// 따라서 실제 Logger 구현체가 아닌 Stub을 활용하기에 알맞다.
```

### Fake objects (가짜 객체)
_Fake objects actually have working implementations, but usually take some shortcut which makes them not suitable for production.  
(Fake 객체는 실제로 동작하는 구현을 갖고 있지만, 일반적으로 실제 프로덕션에는 적합하지 않은 몇 가지 지름길을 사용한다.)_

- **진짜 객체의 행동을 흉내** 내지만, 진짜 객체를 사용할 때 생기는 **부수 효과(side effect) 등이 일어나지 않도록 경량화하고 최적화**한 것
- Fake 객체의 쓰임새를 가장 잘 보여주는 예는 _영속성_.
```java
public interface UserRepository {
    void save(User user);
    User findById(long id);
    User findByUsername(String username);
}
```
- 위 예제에서 만약 테스트 더블을 사용하지 않는다면, 항상 실제 데이터베이스에 access하려 할 것이다.
- 그렇다고 `UserRepository`의 stub을 활용하려니, 모든 시나리오에 따라 stub을 만들어주는 것은 한계가 있다. **입력값에 따라 다르게 행동하는 테스트 더블이 필요하다**.
- 이럴 때 아래와 같이 Fake 객체로 '_초간단 in-memory 데이터베이스_'를 만들어서 쓰자.
```java
public class FakeUserRepository implements UserRepository {
    private List<User> users = new ArrayList<>();

    public void save(User user) {
        if (findById(user.getId()) == null) {
            users.add(user);
        }
    }

    public User findById(long id) {
        for (User user : users) {
            if (user.getId() == id) return user;
        }
        return null;
    }

    public User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
```
- 이렇게 대용품을 만들어 사용하면 **진짜 객체의 행동을 흉내** 내지만, 진짜보다 훨씬 빠르게 동작한다.



### Spies (스파이)
_Spies are stubs that also record some information based on how they were called. One form of this might be an email service that records how many messages it was sent.  
(Spy는 어떻게 호출되느냐에 따라 일부 정보를 기록하기도 하는 stub이다. 발송된 메시지 수를 기록하는 'email service'가 Spy의 한 형태가 될 수 있다.)_

- 

### Mocks (Mock 객체)
_Mocks are pre-programmed with expectations which form a specification of the calls they are expected to receive. They can throw an exception if they receive a call they don't expect and are checked during verification to ensure they got all the calls they were expecting.  
(Mock 객체는 자신이 받고자 하는 호출의 spec을 형성하고자 하는 의도로 미리 프로그램된다. Mock 객체는 예상치 못한 호출을 받았을 때 예외를 던질 수 있고, 기대했던 호출이 모두 수행되었는지 검증(verification) 단계에서 확인한다.)_

- 