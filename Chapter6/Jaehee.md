# Chapter 6 신뢰성  

- 신뢰할 수 있는 코드를 만들기 위해서는 테스트 자체도 믿음직 해야 한다.   

## 테스트의 신뢰성에 문제를 주는 요인  
### 1. 주석으로 변한 테스트  
- 전체 혹은 일부를 주석 처리한 테스트
  - 의도를 알아내기 어렵다 -> 생산성 저하
  - 구현에 대한 검증이 불가하고 설계 지침으로써의 역할도 하지 못한다  
 
- 개선 방법  
  1. 목적을 이해하고 리팩토링 시도  
  2. 실패시 삭제  

### 2. 오해를 낳는 주석  
- 코드의 실제 동작과 다른 내용의 주석  

- 개선 방법  
  1. 주석 대신 적절한 변수명이나 메서드 명을 사용해라  
  2. 주석으로 설명하려던 코드 블럭을 메서드로 추출하고 알맞은 이름을 지어줘라  

- 코드의 의도를 전달할 책임은 코드 자신에 있다. 주석으로 코드를 설명한다는 생각 자체에 근본적인 문제가 있다는 뜻이다.  

- 좋은 주석
  - '무엇을'이 아닌 '왜'를 설명하는 주석  
  - 코드가 그렇게 작성될 수 밖에 없던 당위성을 설명하는 주석  

### 3. 절대 실패하지 않는 테스트  
- 문제가 있어도 알려주지 못하는 테스트는 의미가 없다.  
- 개선 방법 
  - JUnit의 `fail()` 
  - JUnit 4의 `@Test (expected = )`

### 4. 지키지 못할 약속  
- 테스트 목적보다 적은 부분을 검사하는 경우  
    1. 아무 일도 안하는 테스트 - 주석이거나 의미없는 메서드인 경우
    2. 검증을 하지 않는 테스트 (Happy path test) - 단언문이 없는 경우  
    3. 의도와 다른 테스트 - 메서드 이름과 다른 경우  
- 개선 방법  
  1. 텅빈 테스트가 낫다 - 아직 구현되지 않았고 약속한 기능이 검증되지 않았음을 명시  
    - `//TODO` 
    - JUnit의 `@Ignore`
  2. 단언문부터 작성한다 - 테스토로 확인하려는 정확한 동작이 무엇인가에 더 집중

### 5. 낮아진 기대치  
- 검증 정확도와 정밀도를 낮춰버린 테스트  
- 예상 동작을 제대로 기술하지 못하는 단언문 때문에 실패해야 할 상황에서도 실패하지 않는다  

- 개선 방법  
  - 명확하고 정교하게 단언하자
  - 하지만 지나치게 구체적이면 픽셀 퍼펙션 문제 발생  

### 6. 플랫폼 편견  
- 모든 플랫폼을 동등하게 다루지 못하는 테스트  
- 개선 방법  
  1. 플랫폼 종속 코드가 있다는 것을 명시  
  2. 특정 플랫폼에서만 수행해야할 기능을 격리하고 그에 대한 테스트 작성  

### 7. 조건부 테스트  
- 조건문 때문에 테스트의 이름이 의미하는 것과 다르게 동작하는 테스트  

- 개선 방법  
  1. 테스트에서 조건문을 찾아내면 모든 갈래가 확실한 실패 조건을 가졌는지 확인  
  2. 엄밀히 말하면 테스트 하려는 시나리오와 동작이 분기별로 서로 다르다면 독립된 테스트로 작성하는 것이 옳다  
  3. 조건 분기는 코드의 실제 동작을 예측하지 못한다는 것이라 가정 하는 내용을 조건문 보다 단언문으로 작성하는 것이 좋다
