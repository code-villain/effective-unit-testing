# Chapter5 유지보수성

- 코드는 쓰이는 횟수보다 읽히는 횟수가 훨씬 많다.
- 현실에서 의 작성은 대부분은 기존코드를 수정하거나 확장하는 걸 뜻한다.
- 유지보수성이 좋아야 하는 이유 (객사오 182쪽)
    > "미래에 대비하는 가장 좋은 방법은 변경을 예측하는 것이 아니라 변경을 수용할 수 있는 선택의 여지를 설계에 마련해 놓는 것이다. ... (중략) ... 언젠가는 변경이 발생할 것이며 아직까지는 그것이 무엇인지 모른다는 사실을 겸허하게 받아들인다."

## 유지보수성을 구리게 만드는 구린 냄새들
### 1. 중복
- 필요없는 반복, 개념과 논리를 곳곳에 흩어놓아 코드를 이해하기 어렵고 불투명하게 만든다.
- 개선 방법 : 중복을 제거한다. 단, 과유불급.

### 2. 조건부 로직
- 단언문이 조건부 (if,while,for)안에 들어가서 실행되지 않을 수도 있는 경우.
- 개선 방법 : 조건문을 회피하거나, 힘들다면 조건부 로직이 실행되지 않은 경우 실패를 알린다.

### 3. 양치기 테스트
- 실패할때도 있고 안할때도 있는 테스트. (경쟁상태를 일으키는 스레드 사용, 날짜나 시간, 성능, 네트워크 환경에 따라 발생 할 수 있다.)
- 개선 방법 
    1. 회피한다.
    2. 제어한다. (더블을 사용해서 항상 일정한 값을 주게 만들도록.)
    3. 격리한다.
    
### 4. 파손된 파일 경로
- 파일경로의 하드코딩, 다른 환경에서는 반드시 실패하게 된다.
- 개선 방법 : 
    1. 경로를 명시할때는 특정 플랫폼(맥, 리눅스, 윈도우)에 종속된 표현을 사용하지 않고, 상대경로를 사용한다. 
    3. 파일을 프로잭트 하위에 위치시킨다.  
    
### 5. 끈질긴 임시 파일
- 임시 파일을 만들었지만, 이 파일이 다음번 테스트에도 영향을 줄 수 있는 경우.
- 개선 방법 :
    1. 파일사용을 자제한다.
    2. @Before 메서드에서 파일을 삭제한다.
    3. 임시파일명을 매 테스트마다 고유하게 만든다.
    4. 파일이 있어야 하는지 없어야 하는지 정확하게 명시힌다.
    
### 6. 잠자는 달팽이
- Thread.sleep()을 사용하는 경우. 성능저하의 문제가 된다. (+양치기 테스트가 될 수도)
- 개선 방법 : 사용하지마! (ex: 작업쓰레드의 종료 여부 확인을 원한다면 java.util.concurrent 사용)

### 7. 픽셀 펗펙션
- 기본타입 단언과 매직넘버의 특수한 형태. 코드의 의도를 알수 없으며, 조금만 틀어져도 실패하는 테스트가 된다.
- 그래픽 분야가 아니더라도 비슷한 예제가 있을 수도 있을것 같다.
- 개선 방법 : 실제 알고리즘을 사용하여 포괄적으로 대조할 수 있도록 한다. (별개의 메소드나 클래스 사용)

### 8. 파라미터화된 혼란
- 여러 인자로 테스트 하기 위한 방법에서 야기되는 혼란. 
    1. 코드가 어수선하여 의도를 알기 어렵고, 
    2. (Junit4 기준) 어떤 인자로 실패하였는지 찾기 어렵다.
- 개선 방법 :
    1. 들여쓰기나 가변인자 메서드를 사용하여 여러 인자를 넣어주는 코드의 의도를 분명히 한다.
    2. 어떤 인자로 실패하였는지 출력하는 자체 메소드를 만든다.
    
### 9. 메서드간 응집력 결핌
- 응집도 문제. 단순히 테스트코드에서만 발생되는 문제가 아니다. 응집도가 떨어지면 가독성이 떨어지고 유지보수를 어렵게 만든다.
    - 한가지의 고유한 기능을 잘 수행하고 있는가?
    - 응집도를 알 수 있는 기준 (클린코드)
        > 각 클래스 메서드는 클래스 인스턴스 변수를 하나 이상 사용해야 한다. 일반적으로 메소드가 변수를 더 많이 사용 할 수록 메서드와 클래스의 응집도가 더 높다. . . . (중략) . . . 응집도가 높다는 말은 클래스에 속한 메소드와 변수가 서로 의존하며 논리적인 단위로 묶인다는 의미기 때문이다.(클린코드 177쪽)
     
- 개선방법 : 클래스를 분리하거나 (혹은 기반 클래스 사용해도 되지만 권장되지 않는다.), 픽스처(필드)들을 줄여볼수 없는지 생각해본다.

---

# 키워드
```
p.133 코드 5-5는
if (...) {
  ...
  return ...:
}
rerurn ...;

else를 안쓰기 위해서 이런 스타일을 최대한 쓰려고 하는 편인데 이것과 유사한 느낌이네요. 
```
- A) 리턴문 한갠데요

```
테스트가 잘 구축되어 있는 시스템의 경우에는 그만큼 팀원들이 테스트라는 백업을 신뢰하고 있을텐데, 조건부 로직이 포함된 테스트는 실제로는 문제가 있음에도 테스트는 성공해버릴 수도 있다는 점에서 큰 문제가 될수도 있겠네요. 특히 복잡한 로직일수록!
```
- A) 좋은 교훈이다.

```
p130 숙제 재밌어요 해보세요
```
- A) 아무도 안했다구요?

```
junit5에서도 어떤 파라미터로 실패했는지 알려주지 않는 혼란이 있나?
```
- A) @DisplayName과 @ParameterizedTest를 사용해서 테스트 이름이 드러나게 할 수 있다고 하네요.
     https://blog.codefx.org/libraries/junit-5-parameterized-tests/

```
P134
테스트메서드는 if else for while switch같은 조건부 실행 구조를 가져서는 안된다.
단위테스트에서 조건부가 꼭 필요한 경우가 혹시 있을까요...??
```
- A) 컬렉션을 다루는 경우 필요하지 않을까요


```
테스트 코드도 리팩토링 해야된다네~
조건문 가급적 쓰지 맙시다 헷갈려요~
파일은 왠만하면 테스트할 때 쓰지 맙시다. 다른 테스트에 영향이 간다네요
동시성은 넘 어렵다...
```
- A) 웹개발하면서 쓰레드를 다룰일이 있을까요?

```
파라미터화된 테스트 기법은 코드를 많이 줄여주지만 논리가 분산되고 테스트 실패시 어떤 데이터에서 실패한지 알기 어렵다. 이거 감안하고 써라
```
- A)

```
파일업로드와 같이 어떤 특정 환경에 종속 되게 하는 테스트는 가급적 하지 말아야 한다.  한다면 그에따른 예외처리를 확실하게 해줘야 한다
```
- A) 파손된 파일경로를 말씀하시는건가요? 어떤 예외처리를 ㅏ말씀하시는거죠? 책에 언급이 없는것 같은데,

```
픽스쳐를 구분지어 테스트 클래스를 둘로 나눈다? 그럼 이 테스트는 검증만을 위한 테스트 인가? 픽스처를 나누는 행위를 한다는 것 나체가 제품 코드도 설계를 변경해야 한다는 것을 말하는건가?
```
- A) ㄴㄴ 아닙니다. 픽스처를 나눈 다는 것은, 응집도를 얘기 하는 겁니다. 설계를 바꾸는게 아니구요 