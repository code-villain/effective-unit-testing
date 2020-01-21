# Chapter5 유지보수성

- 코드는 유연하고, 그래서 **망가지기도 쉽다**.
- 테스트도 마찬가지라서, **자동화된 단위 테스트를 작성할 때도 이런 취약성에 주의하면서 관리해야 한다**.

---
## 중복
- 하나의 개념이 여러 차례에 걸쳐 표현되거나 복제된 것. **필요 없는 반복**.
- 예시
  - 반복되는 특정 숫자값과 문자열
  - 곳곳에 흩어진 하드코딩된 값
  - 두 클래스나 객체 혹은 메서드의 역할이 겹침
  - 모양은 다르지만 기능이 같은 코드 조각
- 중복이 나쁜 이유
  - 개념과 논리를 곳곳에 흩어놓아 **코드를 이해하기 어렵고 불투명하게 만든다**.
  - 코드를 수정하려면 **중복된 곳을 모두 찾아 일일이 고쳐야** 한다.
- 결국 우리의 최우선 목표는 **코드를 읽기 쉽게 유지하여 읽는 이에게 그 의도와 기능을 명확히 전달**하는 것이다.
  - 이를 고려할 때, **가독성을 위해 일부러 중복을 남겨둬야 할 상황도 있다**는 것을 이해해야 한다.
### 중복의 종류
- 상수 중복
  ```java
  public class TemplateTest {
    @Test
    void emptyTemplate() throws Exception {
      assertThat(new Template("").evaluate()).isEqualTo("");
    }

    @Test
    void plainTextTemplate() throws Exception {
      assertThat(new Template("plaintext").evaluate()).isEqualTo("plaintext");
    }
  }
  ```
  - 각각의 테스트에서 빈 문자열과 "plaintext"가 두번 씩 사용되었다.
  - 상수 중복은 **지역 변수로 만들어서 제거**한다.
- 구조 중복
  - 위의 상수 중복을 지역 변수로 만들어서 제거하면 다음과 같다.
  ```java
  public class TemplateTest {
    @Test
    void emptyTemplate() throws Exception {
      String template = "";
      assertThat(new Template(template).evaluate()).isEqualTo(template);
    }

    @Test
    void plainTextTemplate() throws Exception {
      String template = "plaintext";
      assertThat(new Template(template).evaluate()).isEqualTo(template);
    }
  }
  ```
  - 그런데, **데이터만 다를 뿐 처리 로직이 똑같은** 상황이다. 이를 구조 중복이라 한다.
  - 구조적 중복을 **사용자 정의 단언 메서드로 추출**하여 해결한다.
  ```java
  public class TemplateTest {
    @Test
    void emptyTemplate() throws Exception {
      assertTemplateRendersAsItself("");
    }

    @Test
    void plainTextTemplate() throws Exception {
      assertTemplateRendersAsItself("plaintext");
    }

    private void assertTemplateRendersAsItself(String template) {
      assertThat(new Template(template).evaluate()).isEqualTo(template);
    }
  }
  ```
- 의미 중복
  - **같은 기능이나 개념을 다른 방식으로 구현한 것**
  - 맨 눈으로는 찾기 어렵다.
```java
@Test
void groupShouldContainTwoSupervisors() {
  List<Employee> all = group.list();
  List<Employee> employees = new ArrayList<>(all);

  Iterator<Employee> i = employees.iterator();
  while (i.hasNext()) {
    Employee employee = i.next();
    if (!employee.isSupervisor()) {
      i.remove();
    }
  }
  assertThat(employees.size()).isEqualTo(2);
}

@Test
void groupShouldContainFiveNewcomers() {
  List<Employee> newcomers = new ArrayList<>();
  for (Employee employee : group.list()) {
    DateTime oneYearAgo = DateTime.now().minusYears(1);
    if (employee.startingDate().isAfter(oneYearAgo)) {
      newcomers.add(employee);
    }
  }
  assertThat(newcomers.size()).isEqualTo(5);
}
```

1. 구조 중복으로 바꾼다.
2. 변수나 메서드를 추출하여 구조 중복을 제거한다.

```java
@Test
void groupShouldContainTwoSupervisors() {
	List<Employee> all = group.list();
	List<Employee> employees = new ArrayList<>(all);

	// for문 사용
	for (Employee employee : group.list()) {
		if (!employee.isSupervisor()) {
			employees.remove(employee);
		}
	}
	// Collection 사용
//		employees.removeIf(employee -> !employee.isSupervisor());
	assertThat(employees.size()).isEqualTo(2);
}

@Test
void groupShouldContainFiveNewcomers() {
	List<Employee> newcomers = new ArrayList<>();
	for (Employee employee : group.list()) {
		LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
		if (employee.startingDate().isAfter(oneYearAgo)) {	// 시작한 지 1년이 안됐을 경우
			newcomers.add(employee);
		}
	}
	assertThat(newcomers.size()).isEqualTo(5);
}
```

---
## 조건부 로직
- 조건부 로직은 아래의 2가지를 어렵게 만들고 오류 발생 가능성을 높인다.
  - 테스트의 의도 파악
  - 코드가 하는 일 혹은 해야 하는 일 파악
- 따라서 **테스트 코드에서 조건부 로직은 피해야 한다**.
  - if, else, for, while, switch 같은 조건부 실행 구조를 갖지 않도록 하자.
  - 테스트는 소비자용 사용설명서보다도 쉬워야 한다.
- 조건문 안에 단언문이 포함되어 있는 경우
  - 어쨌든 조건문을 모두 거쳐갈 테니, 그 안의 단언문도 당연히 실행될 것이라 착각하기 쉽다.
  - 하지만 실제로는 **하나도 실행되지 않을 수 있다**.
  - **그래도 테스트는 성공할 수도 있다**는 것이 큰 문제다.
### 개선 방법
- 일단 간소화부터 시도하는 게 좋다.
  - 각 조건문에 해당하는 코드 블럭을 적절한 이름의 메서드로 추출하는 방법을 추천한다.
  - 클린 코드와 관련된 내용을 보면, 가독성을 위해 `else`를 쓰지 말라는 얘기를 종종 한다. 다음과 같다.
    ```java
    if (...) {
      ...
      return "A";
    }
    return "B";
    ```
  - 조건부 단언의 개선도 유사한 패턴이다.
    ```java
    if (...) {
      assertThat(...);
      return;
    }
    fail(...);
    ```
    - 즉 조건문 안으로 들어가지 않았다고 해서 테스트가 무조건 성공해버리는 케이스를 방지하기 위함이다.

---
## 양치기 테스트

---
## 파손된 파일 경로

---
## 끈질긴 임시 파일

---
## 잠자는 달팽이
- 다른 스레드가 완료되기를 기다리느라 `Thread#sleep`으로 긴 시간을 허비한 후에야 다음 단계를 진행하는, 아주 느릿느릿한 테스트
- 느려터진 테스트는 유지보수 입장에서는 치명적인 단점이다. 

### 개선 방법
- 코드에서 `Thread#sleep` 호출문을 찾아보고, 이례적으로 느린 테스트가 있는지 지켜보자.
- 하지만 `Thread#sleep`을 바로 제거하기는 쉽지 않다는 점이 문제다.
  - 테스트 스레드는 작업 스레드가 일을 마치는 즉시 알 수 있어야 한다.
  - `CountDownLatch` (java.util.concurrent) 객체를 사용하여, 작업 스레드와 테스트 스레드 사이를 조율한다.

---
## 픽셀 퍼펙션

---
## 파라미터화 된 혼란
- 파라미터화 된 테스트 패턴(Parameterized Test Pattern)
  - 데이터를 아주 조금씩만 바꿔가며 수차례 반복 검사하는 데이터 중심 테스트가 있을 때 중복을 없애주는 기법
- parameterized test는 _입력값과 출력값만 다른 다수의 테스트가 반복되는 걸 간결하게 줄여주는_ 멋진 패턴이지만, 과도하게, 잘못된 상황에서 사용하면 테스트 냄새가 된다.  
(= 파라미터화 된 혼란; parameterized mess)
  - 논리도 분산되고 테스트를 실패하게 만든 데이터가 무엇인지 찾기 어려워서, 코드를 간소화해 얻은 이점이 상쇄되는 경우도 많다.

### 문제점
- (JUnit 4 기준) 파라미터 집합의 리스트는 `@Parameters`가 붙은 메서드로부터 반환된다.
  - 문제는 **파라미터의 수와 복잡도가 증가하고 리스트가 길어질 수록 너무 어수선해진다**는 점이다.
- Parameterized 테스트 러너가 동적으로 자동 생성한 테스트는, 근본적으로 익명이며 수행된 순서 외에는 구별할 수 있는 표식이 전혀 없다.
  - 그래서 **어떤 파라미터 집합을 이용한 테스트가 실패한 것인지 알아내기 어렵다**.
  - 단언문의 실패 메시지에 개별 테스트 케이스의 식별자를 추가하는 방법이 있기는 한데, "**과연 꼭 parameterized 테스트 패턴을 적용해야만 하는 걸까**"라는 숙고를 해 볼 필요가 있다. 즉, **parameterized 테스트 패턴은 가급적 쓰지 말자**.
  - 다만 이 문제는 JUnit 5부터는 `@DisplayName`과 `@ParameterizedTest`를 이용해서 해결할 수 있는 것으로 보인다([참조 링크](https://blog.codefx.org/libraries/junit-5-parameterized-tests/)).

---
## 메서드 간 응집력 결핍
