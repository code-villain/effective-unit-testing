# Chapter6 신뢰성

- 신뢰할 수 있는 코드를 만들기 위해 테스트를 작성한다.
- 그렇다면 **테스트 자체도 믿음직해야 한다**.
- 만약 **테스트를 믿지 못한다면, 제품 코드를 쉽사리 바꿀 수 없을 것**이다.
- 6장에서는 테스트의 신뢰성에 대해 살펴본다.

---
## 주석으로 변한 테스트
- **전체 테스트를 주석 처리**해버린 경우
- 기본적으로, **왜 주석 처리되어 있는지 알 길이 없다**.

### 개선 방법
- 일단 커밋 이력을 살펴보든 그 테스트에 대해 알만 한 사람을 찾아 물어본다. 그게 안된다면 다음을 시도한다.
1. *주석 처리한 이유*를 이해하려 노력해보고 검증해본다.  
성공했다면, 주석을 풀고 파악한 목적이 더 잘 표현되게끔 **리팩토링**한다.
2. 실패했다면, **미련 없이 지워버린다**.  
**주석 처리한 이유를 바로 알아내지 못했다면, 앞으로도 영원히 알아내지 못할 가능성이 높다**. 그러니 깨끗이 제거하는 게 낫다.

---
## 오해를 낳는 주석
- **코드의 실제 동작과는 다른 내용**이 적혀있는 주석
- 주석으로 인해 코드를 오해한 채로 진행했다가, 디버깅으로 시간을 버리게 될 수도 있다.
- 주석을 처음 작성했을 때는 의미 있고 올바른 내용을 담고 있더라도, 시간이 흐르고 코드가 변해가면서 서서히 제멋대로인 주석으로 변해간다. 코드와는 달리 **직접 실행되지 않기 때문에, 그만큼 변질되기도 쉽다**.

### 개선 방법
- 코드의 의도를 전달할 책임은 **코드 자신**에 있다. **주석으로 코드를 설명한다는 생각 자체가 잘못**된 것이다.
- 주석이 쓰인 이유가 코드의 동작을 설명하기 위함이거나, 주석 없이는 동작을 이해하기 어려운 코드가 있다면, 다음과 같이 **코드를 리팩토링해야 한다**는 뜻이다.
1. ~~주석~~ 대신 **더 적절한 변수명이나 메서드명**을 사용한다.
2. 주석으로 설명하려던 **코드 블록을 메서드로 추출**하고, 알맞은 이름을 지어준다.
- 좋은 주석은 ~~what~~이 아니라 **why**를 설명한다.
  - 코드가 무엇(what)을 하는지 설명한다면 무조건 code smell이다.
  - 그런 **주석이 필요 없을 만큼 쉽게 읽을 수 있는 코드**를 작성해야 한다.
  - 좋은 주석이란 코드가 그렇게 작성될 수밖에 없었던 당위성(**why**)을 설명하는 주석이다.

---
## 절대 실패하지 않는 테스트
- 테스트를 통과했으니 문제 없을 것이라는 잘못된 인식을 심어줄 수 있으므로, 차라리 없느니만 못하다.

### 1. 예외가 발생하길 기대하는 테스트
```java
@Test
void includeForMissingResourceFails() { // 예외 발생을 기대하는 테스트
  try {
    new Environment().include("실존하지 않는 자원");
    fail(); // 예외가 발생하지 않았을 때!
  } catch (IOException e) {
    assertThat(e.getMessage()).contains("실존하지 않는 자원");
  }
}
```
- 만약 위 코드에서 `fail()`이 없다고 가정해보자. 예외가 발생하면 catch 블록으로 들어가 테스트가 성공하는데, 예외가 발생하지 않아도 역시 테스트가 성공한다. 혹시나 제품 코드에 문제가 있어도 전혀 알아챌 수 없다.
- 예외가 발생하길 기대하는 테스트를 작성할 때는, **예외가 발생하지 않았을 때 반드시 `fail()`을 호출해야 한다**.
- JUnit 4의 경우, `@Test`의 `expected` 속성을 사용하면 더 깔끔하다(JUnit 5는 방법이 다름).
  ```java
  @Test(expected = IOException.class)
  public void includeForMissingResourceFails() {
    new Environment().include("실존하지 않는 자원");
  }
  ```
  - 다만 위의 예제에 비교하면, **예외 객체에 접근할 기회가 사라져서 깊이 있는 단언은 불가능**하다.

### 2. 단언문이 하나도 없는 테스트
- 테스트 이름에 걸맞게 적절한 단언문을 추가하면 되겠다.
- '지키지 못할 약속' 문제의 2번째 유형의 해결책처럼, 테스트를 작성할 때 단언문부터 적는 습관도 좋겠다.

---
## 지키지 못할 약속
- **테스트가 자신이 내세운 것보다 훨씬 적은 것을 검사**하거나, 심지어 아무 것도 검사하지 않는 경우
1. 아무 일도 안하는 테스트
```java
@Test
void filteringObjects() throws Exception {
  // ...
  // ...
  // ...
  // ...
  // assertThat(...);
}
```
- 이 테스트는 아무 일도 안하지만 성공할 것이므로, 개발자는 *filteringObjects*라는 **기능이 정상적으로 구현되었다고 오해**할 수 있다.
- **개선 방법**
  - 주석 처리된 테스트보다는 **텅 빈 테스트**가 차라리 낫다.
  - JUnit의 `@Ignore`를 붙여둔다.
    - **왜 테스트가 ignore 처리된 것인지**를 `@Ignore`의 value 속성에 명확히 적어두는 것이 좋다. 기존에 잘 돌아가고 성공하던 테스트가 갑자기 실패한다고 해서 `@Ignore`를 붙여놓고 이유도 써놓지 않으면, 영영 돌아오지 못할지도 모른다.
      - [SonarSource: "Tests should not be ignored"](https://rules.sonarsource.com/java/RSPEC-1607)
    - Junit 5에서는 `@Disabled`를 사용한다.
2. 아무 것도 검사하지 않는 테스트
```java
@Test
void cloneRetainsEssentialDetails() throws Exception {
  Document model = ModelFactory.createDocument();
  model.schedule(new Transaction("Test", date("2020-01-26"), -1));
  model = model.clone();
  Transaction tx = model.getScheduledTransactions().get(0);
}
```
- 그 **어떤 단언문도 가지고 있지 않다**. 따라서 **기능이 어떻게 동작하건 항상 성공**한다.  
(예외가 발생할 때만 실패함)
- 일명 happy path test
- **개선 방법**
  - 단언문이 있는지 확인해본다.
  - 단언문을 빼먹지 않도록 해주는 간단한 습관
    - **테스트를 작성할 때 단언문부터** 적는다.
    - 테스트로 확인하려는 정확한 동작이 무엇인가에 더 집중하게 해주는 효과도 있다.
3. 이름값 못하는(약속한 것을 다 확인하지 않는) 테스트
```java
@Test
void zipBetweenTwoArraysProducesAHash() throws Exception {
  Array keys = new Array("a", "b", "c");
  Array values = new Array(1, 2, 3);
  Array zipped = keys.zip(values);
  assertNotNull("We have a hash back", zipped.flatten());
}
```
- 테스트 이름(_배열 두개를 압축해서 해시값을 만든다_)과 실제 테스트에서 확인하는 것(_`zip()`으로부터 반환받은 배열을 flatten한 객체가 null이 아니다_)이 전혀 다르다.
- **개선 방법**
  - 단언문부터 적는 습관
  - 테스트를 완성하기 전까지 테스트의 이름을 아예 비워두거나, TODO() 같은 임시명을 쓴다.

---
## 낮아진 기대치
- **테스트가 성공하기 위해 요구되는 기준치(기대치)를 낮춤**으로써 게으르고 쉬운 길을 택하는 경우
- 검증 정확도와 정밀도를 낮춘다.
- 실패해야 할 상황에서도 실패하지 않는, 지나치게 강건한 테스트를 만들어낸다. 개발자가 잘못된 안도감에 빠져들도록 할 수도 있다.
```java
@Test
void complexityForSourceFile() {
  double sample1 = complexity.of(new Source("test/Sample1.java"));
  double sample2 = complexity.of(new Source("test/Sample2.java"));
  assertThat(sample1).isGreaterThan(0.0);
  assertThat(sample2).isGreaterThan(0.0);
  assertTrue(sample1 != sample2);
}
```
- 위 예제의 단언문들을 보면, 두 결과가 0보다 크고 값이 서로 다르기만 하면 테스트가 무조건 성공한다.
- 즉 **단언문이 너무 모호**해서 요구하는 게 무엇인지 알기 어렵다.
- 또한 이 테스트는 **변화에 너무 둔감해서, 실패해야 할 상황에서도 실패하지 않는다**.

### 개선 방법
- **기준을 다시 높여서 예상한 바를 정확하게 검사**한다.
```java
@Test
void complexityForSourceFile() {
  assertThat(complexity.of(new Source("test/Sample1.java"))).isEqualTo(2);
  assertThat(complexity.of(new Source("test/Sample2.java"))).isEqualTo(5);
}
```
- 그렇다고 완전무결한 정확성만이 꼭 미덕은 아니다(→ 픽셀 퍼펙션). **테스트에 가장 적절한 추상화 수준을 고민해야 한다**.

---
## 플랫폼 편견
- 필요한 **모든 플랫폼을 동등하게 다루지 못하는** 테스트 냄새
- '**조건부 테스트**'의 한 형태
```java
@Test
void knowsTheSystemDownloadDirectory() throws Exception {
  ...
  Platform platform = Platform.current();

  if (platform.isMac()) { // Mac OS X일 때
    ...
    assertThat(...);
  } else if (platform.isWindows()) {  // Windows일 때
    ...
    assertThat(...);
  }
}
```
- 위 예제의 문제점
  - Mac OS X나 Windows가 아닌 다른 플랫폼에서 테스트를 돌려도 어쨌든 통과한다.
  - `Platform.current()`은 테스트를 돌리는 플랫폼의 정보를 받아오는 것이므로, 결국 if-else절 중 단 하나에 대해서만 테스트가 동작할 것이다.

### 개선 방법
- 플랫폼별로 테스트를 여러 개로 나눠서, 각각의 경로를 외부로 노출한다.
- JUnit의 Assumption(가정) API를 활용한다.
  - 가정과 다르면 해당 테스트는 더 진행되지 않는다.
```java
public class TestConfiguration {
  Platform platform;
  String downloadsDir;

  @Before
  void setUp() {
    platform = Platform.current();  // 아직 해결되지 않은 부분
  }

  @Test
  void knowsTheSystemDownloadDirectoryOnMacOsX() {
    assumeTrue(platform.isMac()); // 의도하지 않은 플랫폼에서는 테스트를 중단
    assertThat(...);
  }

  @Test
  void knowsTheSystemDownloadDirectoryOnWindows() {
    assumeTrue(platform.isWindows()); // 의도하지 않은 플랫폼에서는 테스트를 중단
    assertThat(...);
  }
}
```
- 다만 `Platform.current()` 코드가 남아 있다. 즉 지금 테스트를 실행하는 플랫폼에 대해서만 테스트가 가능하다는 문제는 여전하다.
  - 제품 코드의 **설계 자체가 문제**가 있음을 알 수 있다.
  - **이 테스트 냄새는 제품 코드의 리팩토링이 필요하다는 신호**로 받아 들이자.
```java
@Test
void knowsTheSystemDownloadDirectoryOnMacOsX() throws Exception {
  String downloadsDir = new MacOsX().downloadDir();
  assertThat(...);
}

@Test
void knowsTheSystemDownloadDirectoryOnWindows() throws Exception {
  String downloadsDir = new Windows().downloadDir();
  assertThat(...);
}
```
- 플랫폼 편견 냄새는 **테스트를 간소화하여 각각의 테스트가 자신에게 필요한 플랫폼을 직접 생성**하도록 리팩토링하면 된다.
- 위 코드에서는 `Platform`의 하위 클래스를 각각의 테스트가 직접 생성한다. 
  - 즉 Platform의 하위 클래스로 MacOsX, Windows, Linux 등을 따로 두도록 제품 코드의 설계를 변경했다.
- 따라서 모든 플랫폼에 대한 테스트를 정상적으로 수행할 수 있게 되었다.
- `Platform.current()`의 동작은 다른 테스트에서 별도로 진행하면 된다.

### Assumption API
- Assumption(Junit 4 기준)이 실패하면 해당 테스트는 더 이상 진행되지 않으며, 결과적으로 테스트는 성공으로 처리된다.
- assumption이 실패하면서 AssumptionViolatedException이 발생했을 때의 모습이다. 테스트가 실패했다고 뜨지는 않지만, 뭔가 이상하다는 것은 감지할 수 있다.
![image](https://user-images.githubusercontent.com/26949964/73132218-49a56880-405b-11ea-9a80-aca39333f109.png)

---
## 조건부 테스트
- 테스트 안에 숨겨진 **조건 때문에 테스트의 이름이 의미하는 것과 다르게 동작**하는 테스트
```java
@Test
public void multipleArgumentsAreSentToShell() throws Exception {
  ...

  if (process.exitCode() == 0) {
    assertThat(process.output().trim()).isEqualTo("hello.txt");
  }
}
```
- if문의 조건이 거짓일 경우에는 단언문이 아예 실행되지 않으면서 테스트가 성공해버린다.
- 테스트는 실패해야 할 땐 실패해야 한다.
```java
public void multipleArgumentsAreSentToShell() throws Exception {
  ...

  assertThat(process.exitCode()).isEqualTo(0);  // 가정 내용을 명시적으로 확인하는 단언문
  assertThat(process.output().trim()).isEqualTo("hello.txt");
}
```
- 그런데, 원래 테스트에서 조건문이 들어있다는 것은 **잠재적으로 하나 이상의 서로 다른 결과를 의도**했다는 뜻이다. 따라서 **각각을 독립된 테스트로 분리**하는 것이 옳다.
```java
public void returnsNonZeroExitCodeForFailedCommands() throws Exception {
  ... // 위 테스트와는 다른 arguments

  assertThat(process.exitCode()).isGreaterThan(0);
}
```
- 위 테스트를 추가함으로써, '종료 코드(exitCode)가 0이길 기대하는 경우'와 '0이 아니길 기대하는 경우'에 대해 모두 테스트를 갖게 되었다.
- **테스트에서 if를 써야 할 이유는 거의 없다**. ~~조건절~~보다는 **가정 내용을 명시적으로 확인하는 단언문**을 대신 사용하는 것이 좋다.