# Chapter3 테스트 더블

## 테스트 더블의 위력
- 어떤 코드 조각이 원하는 동작을 올바로 수행하는지 검증하려 할 때, 주변 코드를 모두 **테스트 더블로 교체**하여 테스트 환경 전반을 통제할 수 있다면 가장 좋다.
![image](https://user-images.githubusercontent.com/26949964/71984289-e601f980-326b-11ea-8507-8919a9062053.png)
- **테스트하려는 코드를 주변에서 분리**하는 것이 테스트 더블을 활용하는 가장 기본적인 이유다.

### 테스트 대상 코드를 격리한다
- "테스트 대상 코드를 격리한다"는 것은, 세상의 모든 것을 다음 2가지로 분류한다는 뜻이다.
    - 테스트 대상 코드
    - 테스트 대상 코드와 상호작용하는 코드
- 즉 **테스트하려는 코드를 그 외의 모든 코드에서 떼어 놓겠다**는 의미.
- 테스트 대상 코드를 격리함으로써,
    - 테스트의 초점이 분명해지고(제어권을 가져온다)
    - 이해하기도 쉬워지고
    - 설정하기도 간편해진다.

### 테스트 속도를 개선한다
- 테스트 더블로 협력 객체를 대체하면, 원본을 그대로 사용할 때보다 빨라진다.

### 예측 불가능한 실행요소를 제거한다
- 예측할 수 없는 요인(= 비결정적 요소 = 결과가 불규칙)을 다뤄야 할 때
    - **테스트 더블**이 해결책이 될 수 있다. 즉, **협력 객체를 활용해서 변수를 제거**한다.
    - 결과에 영향을 주는 모든 **(비결정적) 요소를 → 결정적으로** 만들기 위함이다.
    - ex) 주사위 객체를 '숫자의 출력 순서가 정해진' 테스트 더블로 대체
    - ex) 시스템 시계 객체를 '항상 똑같은 시간을 알려주는' 테스트 더블로 대체

### 특수한 상황을 시뮬레이션한다
- 테스트 실행 도중 컴퓨터의 네트워크 인터페이스가 비활성화되는 상황을 시뮬레이션 하고 싶은 경우
    - 연결 요청을 처리하는 부분을 테스트 더블로 대체해서 예외를 발생시키면 된다.

### 감춰진 정보를 얻어낸다
- 테스트가 얻을 수 없었던 정보에 접근할 수 있다.  
_(하단의 "테스트 더블의 종류"에서 등장하겠지만, **Spy**와 관련된 내용이다.)_

---
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
- 테스트 더블이 최소한의 행동은 취해주거나 입력값에 따라 다르게 행동했으면 할 때
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
(Spy는 어떻게 호출되느냐에 따라 일부 정보를 기록하는 stub이다. 발송된 메시지 수를 기록하는 'email service'가 Spy의 한 형태가 될 수 있다.)_

- 기본적인 테스트 더블인 Stub과 Fake 객체만으로는 해결할 수 없는 상황일 때
- Spy는 **입력 인자로 사용되는 객체가, 테스트에 필요한 정보를 알려주는 API를 제공하지 않을 때** 유용하다. 
- Spy는 **목격한 일을 기록해 두었다가 나중에 테스트가 확인할 수 있게끔** 해준다.  
이것만 봐서는 무슨 말인지 이해가 잘 안된다. 예제를 보자.
```java
public class DLog {
    private final DLogTarget[] targets;

    public DLog(DLogTarget... targets) {
        this.targets = targets;
    }

    public void write(Level level, String message) {
        for (DLogTarget each : targets) {
            each.write(level, message);
        }
    }
}

public interface DLogTarget {
    void write(Level level, String message);
}
```
- 위 예제에서 테스트 대상은 `DLog`다.
    - 생성자에서 `targets`를 받는다.
- `DLog`에서 실질적으로 테스트하고자 하는 것은 `DLogTarget`에서 `write()`를 제대로 수행하였는지의 여부다. 그런데 `DLogTarget`에는 `write()`만 선언되어 있을 뿐, 테스트에 필요한 추가적인 메서드(API)가 없다.
- `DLogTarget`을 대체하는 `SpyTarget`이라는 Spy를 만들자.
```java
public class DLogTest {
    @Test
    public void writesEachMessageToAllTargets() throws exception {
        SpyTarget spy1 = new SpyTarget();
        SpyTarget spy2 = new SpyTarget();
        DLog log = new DLog(spy1, spy2);    // 1) Spy 잠입! (spy는 DLogTarget을 대체함)
        log.write(Level.INFO, "message");

        // 3) Spy가 잠입 결과를 보고한다.
        assertThat(spy1.received(Level.INFO, "message")).isTrue();
        assertThat(spy2.received(Level.INFO, "message")).isTrue();
    }

    // 이 Spy는 DLogTarget을 대체하는 테스트 더블이다.
    private class SpyTarget implements DLogTarget {
        // 2) write() 내부에서 정보를 기록해둔다(추적한다).
        private List<String> log = new ArrayList<>();

        @Override
        public void write(Level level, String message) {
            log.add(concatenated(level, message));
        }

        // 테스트를 위한 API로서 작용한다.
        boolean received(Level level, String message) {
            return log.contains(concatenated(level, message));
        }

        private String concatenated(Level level, String message) {
            return level.getName() + ": " + message;
        }
    }
}
```

### Mocks (Mock 객체)
_Mocks are pre-programmed with expectations which form a specification of the calls they are expected to receive. They can throw an exception if they receive a call they don't expect and are checked during verification to ensure they got all the calls they were expecting.  
(Mock 객체는 자신이 받고자 하는 호출의 spec을 형성하고자 하는 의도로 미리 프로그램된다. Mock 객체는 예상치 못한 호출을 받았을 때 예외를 던질 수 있고, 기대했던 호출이 모두 수행되었는지 검증(verification) 단계에서 확인한다.)_

- Mock 객체는 **특수한 형태의 Spy**다.
    - Spy가 잠복 경찰이라면, Mock 객체는 갱단 본거지까지 침투한 원격 조정 사이보그다.
- **특정 조건이 발생하면, 미리 약속된 행동**을 취한다.
- Mock 객체를 사용하면, 예기치 않은 일이 발생하자마자 바로 실패하는, 훨씬 정교한 테스트도 가능하다.
```java
public class TestTranslator {
    @Test
    public void usesInternetTranslation() throws Exception {
        // Mock 객체 생성
        final Internet internet = mock(Internet.class);

        // Mock 객체(internet)의 임무: 테스트 중에 발생할 일, 그 일이 발생했을 때의 행동지침
        // Mock 객체(internet)가 특정 문자열을 포함한 인자를 받아 get()을 호출하면, 약속된 문자열을 반환해야 한다.
        given(internet.get(argThat(containsString("langpair=en%7Cfi"))))
            .willReturn("{\"translatedText\":\"kukka\"}");

        Translator translator = new Translator(internet);   // 검사할 Translator에 Mock 객체를 넘긴다.
        String result = translator.translate("flower", ENGLISH, FINNISH);

        // Assertion(단언)
        assertThat(result).isEqualTo("kukka");
    }

    ...
}
```
- 위 예제에서, `usesInternetTranslation()`의 맨 하단에 있는 것만이 assertion이 아니다.
    - `given()` 부분에서 Mock 객체의 임무를 정해놓았으니, **Mock 객체는 기대한 동작이 정말로 일어나는지 두 눈을 부릅뜨고 지켜보고 있다**.

---
## 테스트 더블 활용 지침

### 용도에 맞는 더블을 사용하라
- 가장 명확한 원칙은 **테스트를 가장 읽기 쉽게 만들어주는 방향으로 선택**해야 한다는 것.
    - 두 객체 간 상호작용의 결과로 특정 메서드가 호출되었는지 확인하고 싶다면, **Mock 객체**를 써야 할 가능성이 높다.
    - Mock 객체를 사용하기로 했는데, 테스트 코드가 생각만큼 깔끔하게 정리되지 않는다면 더 단순한 **Spy**를 손수 작성해서도 똑같은 마술을 부릴 수 있는지 생각해보자.
    - 협력 객체는 자리만 지키면 되고 협력 객체가 대상 객체에 넘겨줄 응답도 테스트에서 통제할 수 있다면 **Stub**이 정답이다.
        - Stub을 쓰기로 했는데, 이미 Mock 객체도 사용 중이라면 Stub도 Mock 객체 라이브러리를 이용해서 생성하자. 어차피 Mock 객체 라이브러리가 제공하는 기능이기도 하고, 테스트 코드로 더 읽기 편해질 것이다.
    - 필요한 서비스나 컴포넌트를 미처 준비하지 못해 Stub을 대용품으로 사용하고 있는데, 시나리오가 너무 복잡해서 벽에 부딪혔거나 테스트 코드가 관리하기 어려울 만큼 복잡해졌다면 **Fake 객체**를 구현하는 걸 고려해보자.
    - 이도 저도 아니라면 동전을 던져 앞면이 나오면 Mock 객체, 뒷면이 나오면 Stub 객체를 쓰자.
- 기억하기 쉬운 테스트 더블 선택 비법
> Stub은 질문하고 Mock은 행동한다.

### 준비하고, 시작하고, 단언하라
- AAA(Arrange-Act-Assert; 준비-시작-단언)
    - 단위 테스트의 구조에 대해 대다수 프로그래머가 동의하는 규약
    - 필요한 객체들을 준비하고 - 실행하고 - 단언한다
- BDD(Behavior-Driven Development; 행위 주도 개발) 진영에서는 '_Given-When-Then_'이라는 구조를 사용한다.
    - 주어진 상황에서(Given), 어떤 일이 발생했을 때(When), 특정 결과를 기대한다(Then).
    - 더 부드럽고 (~~구현~~이 아닌) *행위*라는 관점에서 사고를 더 자연스럽게 표현할 수 있다.
- 세 영역 중 하나가 **비대하다**고 느껴진다면, **너무 많은 것을 한꺼번에 검사하려는 테스트일 가능성이 높다**.
    - 따라서 **더 작은 단위의 기능을 집중적으로 검사하는 테스트로 나눌 필요가 있다는 신호**다.

### 구현이 아니라 동작을 확인하라
- 테스트는 무언가 잘못 변경되면 즉시 실패해서 우리에게 알려줄 거라는 믿음을 주어야 한다.
    - 그렇다고 해서, **검증 목적과 관련 없는, 지극히 사소한 변경마저도 테스트를 실패하게 만들면 안된다**.
- 테스트는 **오직 한 가지만 검사**해야 하고 그 **의도를 명확히 전달하도록** 작성되어야 한다.
    - 그러니 **굳이 확인할 필요 없는 부수적인 구현**을 테스트에 포함하고 있지는 않은지 자문해봐야 한다.
- ~~구현~~이 아니라 **동작**을 검증하자.

### 자신의 도구를 선택하라
- 어떤 Mock 객체 라이브러리를 선택할지는 거의 **개인 취향의 문제**
- 중요한 것은 **테스트 코드는 지금이나 앞으로나 읽기 쉽고 간결하고 관리하기 쉬워야 한다**는 것

### 종속 객체를 주입하라
- 종속 객체를 다른 객체로 교체해야만 쉽게 검사할 수 있다면, 해당 의존 객체의 초기화는 그 객체가 사용되는 곳이 아닌 다른 곳에서 이뤄져야 한다.
    - 종속 객체를 private 필드에 저장하거나, 팩토리 메서드 등을 통해 외부로부터 얻도록 한다.
- 종속 객체를 고립시켰다면, 이제 그 객체에 접근할 방법이 필요하다.
    - **의존성 주입(Dependency Injection)**을 통해 종속 객체를 외부에서 안으로 집어넣는 방법이 좋다.
    - 생성자 주입 방식을 권장 (Translator 예제 참고)