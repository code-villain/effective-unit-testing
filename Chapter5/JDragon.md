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


### 5.3 양치기 테스트

