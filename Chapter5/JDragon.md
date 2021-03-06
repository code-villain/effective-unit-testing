# 05 유지보수성

아주 사소한 수정만으로도 전체를 완전히 망쳐버릴 수 있다.
자동화된 단위 테스트를 작성할때도 이런 취약성에 주의하면서 관리해야 한다.

## 5.1 중복

중복 : 하나의 개념이 여러 차례에 걸쳐 표현,복제 된 것. 즉, 필요없는 반복이다

중복이 나쁜 이유
- 개념과 논리를 흩어놓아 코드 이해를 어렵게 한다
- 코드 수정시 중복된 곳 모두 찾아서 수정해야한다

### 5.1.1 예시

상수 중복

### 5.1.2 개선 방법

assertEquals("", new Template("").evaluate() );
assertEquals("plain" , new Template("plain").evaluate() );

문자열을 지역변수(template)로 추춣한 다음 두 테스트 메서드를 다시 보면

assertEquals(template , new Template(template).evaluate() );
assertEquals(template , new Template(template).evaluate() );

로 동일한 구조의 단언문인것을 확인할 수 있다. 데이터만 다를 뿐, 처리 로직이 같은 구조 중복이다

구조적 중복을 추출하여 사용자 정의 단언 메서드로 만들면 구조 중복도 제거 가능하다.


### 5.1.3 정리

중복 때문에 코드가 분산되면 테스트 유지보수에 긴 시간이 허비된다.
상수값을 지역변수로 추출하는 방법은 눈앞의 데이터 중복을 제거함과 동시에 숨어있는 구조 중복을 드러내는 멋진 기술이다


## 5.2 조건부 로직

자동화된 테스트 용도
- 코드를 망쳐버리면 경고해준다
- 코드에 기대하는 동작을 정할 때 테스트를 돌려 확인할 수 있다
- 코드가 하는 일 혹은 해야 하는 일을 파악할 때 유용하다

리팩토링 중에 모든 기능이 여전히 정상인지 확인하고자 테스트를 돌린다.
(테스트 코드가 있어야 리팩토링 과정중에 안정성?을 보장할 수 있다)


### 5.2.1 예시

~~~ java
  @Test
  public void returnAnIteratorForContents() {

    // 코드 모양만 기억하기 위해서 작성함.

    Dictionary dictionary = new Dictionary();
    dictionary.add("A" , new Long(3));
    dictionary.add("B","21");
    for (Iterator e = dictionary.iterator() ; e.hasNext();) {
      Map.Entry entry = (Map.Entry)e.next();
      if ("A".equals(entry.getKey())) {
        assertThat(entry.getKey()).isEqualTo(3L);
      }

      if ("B".equals(entry.getKey())) {
        assertThat(entry.getKey()).isEqualTo("21");
      }

    }

~~~

데이터를 넣고 내용물을 확인하는 코드이다. 사소한 기능 하나를 검사하는 코드가 해독하기 어렵게 짜여있다


### 5.2.2 개선방법

복잡한 코드를 다룰 때는 무엇보다 간소화부터 시도해라.
코드 블록을 적절한 이름의 메서드로 추출하는 방법을 사용하자

사용자 정의 단언 메서드에에 복잡한 부분을 추출했다. 이로 인해 테스트의 의도가 더 잘 드러나게 되었다.

텅빈 Iterator 를 넣어도 실패하지 않는 단언문이다.
for 순환문을 잘못 사용했기 때문이다

기대했던 결과를 찾으면 단언문에서 반환하게 하고
찾지 못하면 테스트를 강제로 실패하게끔 만들었다.


### 5.2.3 정리

코드가 해야 할 일과 동작을 파악할 때 테스트를 활용한다.
우리가 실수하면 테스트가 바로 알려줄 거라 믿고 안심하고 코드를 고치기도 한다.

코드를 수정하려면 우선 이해를 해야하는데, 조건부 로직이 들어가면 코드 이해가 어려워진다.

결론 : 테스트 메서드에서는 if, else, for , while, switch 와 같은 조건부 실행구조를 가져서는 안된다


## 5.3 양치기 테스트

지독한 테스트 냄새!

매번 실패하는데 대수롭지 않게 취급하는 테스트
- 코드를 잘못 고쳐서 테스트가 실패한다면 확인하려던 어떤 동작이 변경되었다는 사실을 알려주어야 한다!
- 매우 중요한 내용...
- 매번 실패하는 테스트는 있으나 마나다. 새로운 어떠한 정보도 알려주지 못한다.

간헐적으로 실패하는 테스트
- 테스트가 실패해서 빌드가 중단 되었는데 다시 돌려보면 운좋게? 통과하는 테스트


### 5.3.1 예시

양치기 대표 예
- 테스트가 경쟁상태를 일으키는 스레드를 사용
- 현재 날짜나 시간에 따라 동작이 달라질 때
- 입출력 속도나 테스트 실행 당시의 cpu 부하 등 컴퓨터 성능에 영향 받을 때
- 네트워크로 원격지의 자원에 접근하는 테스트 (네트워크 장애시 양치기로 변신)

책의 예제는 타임 스탬프와 관련된 것.
맥 OS 에서는 반드시 실패하는 테스트다.

### 5.3.2 개선 방법

파일의 타임 스탬프를 직접 명시해준다.

불명확하고 비 결정적인 요소에 의지하는 대신
어떤 플랫폼, 어떤 컴퓨터에서도 안정되게 동작하게 된다.

### 5.3.3 정리

문제를 피해서 살짝 돌아간다.
비결정적 행위의 원인을 제어한다.
골치 아픈 코드만 따로 격리 조치한다.

## 5.4 파손된 파일 경로


파일 시스템의 특정 경로를 명시적으로 참조하는 코드를 짜지 말자.

### 5.4.1 예시

윈도우에 종속된 절대 경로를 사용했다.

### 5.4.2 개선 방법

윈도우, 리눅스 모두에서 동작하는 코드를 짜라.
상대경로를 사용해라.

new File("./src/test/~~~/data.xml");

### 5.4.3 정리
프로젝트에 필요한 자원은 프로젝트 루트 디렉토리의 하위에 두는 걸 원칙으로 하자
테스트 코드에서만 사용하는 데이터 파일은 테스트 코드와 같은 위치에 두고 클래스패스로 접근하면 된다.


## 5.5 끈길긴 임시파일

- 임시파일 : 임시로 쓰고나면 버리는 파일, 일회용 파일
- 파일 사용은 무조건 최소한으로 자제해야 한다.

지침
- @Before 메서드에서 파일을 삭제해라
- 가능하면 임시 파일명도 고유하게 지어알
- 파일이 있어야 하는지를 명시해라

## 5.6 잠자는 달팽이

테스트를 느려지게 하는건 파일 IO 만이 아니다.
스레드 sleep 은 기대하는 결과나 부수효과를 얻기 위해 다른 스레드가 일을 끝마치기를 기다리기 위해 사용된다

개선 방법
java.util.concurrent 패키지의 동기화 객체를 이용하면 테스트 스레드는 작업 스레드가 일을 긑마치는 즉시
알아낼 수 있다.

테스트 스레드는 작업 스레드가 일을 마치는 즉시 알 수 있어야 한다

## 5.7 픽셀 퍼펙션

점과 좌표보다 고차원 개념으로 추상화하여 단언해라.

## 5.8 파라미터화된 혼란

파라미터화된 테스트 패턴이란 데이터를 아주 조금씩만 바꿔가며 수차례 반복 검사하는 데이터 중심 테스트가 있을 때 중복을 없애주는 기법이다.

### 5.8.1 예시

파라미터화된 테스트를 사용하여 중복을 제거했지만 다소 난해한 코드와 어수선한 문법이 사용되었다.

### 5.8.2 개선 방법

파라미터화 테스트를 사용하지마라?

그냥 여러 테스트 케이스를 하나의 테스트 메서드로 몰아넣어라
(한 테스트 메서드 안에 단언문을 여러개 만들어라)

가독성을 향상시켜줄 수 있다면 파라미터화된 테스트를 사용해도 된다?

중첩 배열로 테스트 데이터를 선언하지 말고 리스트롤 사용해서 하나씩 더해줘라
들여쓰기를 잘 이용해라

개별 테스트 케이스의 식별자를 단언문의 실패 메시지에 추가해라

### 5.8.3 정리

파라미터화된 테스트 패턴은 입력값과 출력값만 다른 다수의 테스트가 반복 되는걸 간결하게 줄여준다. 이
패턴이 가장 많이 활용되는 영역은 검증, 변환, 문자열 처리와 관련된 곳에서.

파라미터화된 테스트 부작용 최소화 하는 법
- 각 데이터 집합을 메서드 호출로 감싸보라
- 들여쓰기를 활용해서 데이터 집합을 구분해라
- 단언문의 오류 메시지를 이용해서 데이터 집합만으로 실패한 테스트를 구분하지 못하는 한계를 극복해라

