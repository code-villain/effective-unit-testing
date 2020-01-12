# Chapter4 가독성

- 테스트란, **코드에 바라는 동작과 가정을 프로그래머 식으로 표현한 것**이다,.
- *테스트를 읽고 난 후*에는 코드가 **해야 할 일**을 이해할 수 있어야 하고, *테스트를 실행한 후*에는 코드가 **실제로 한 일**이 무엇인지 말할 수 있어야 한다.
- 테스트의 핵심은 **단언문**(**Assertion**)이며, 대상 코드의 올바른 동작을 규정한다.
  - **테스트의 성공 여부를 판별**하는 역할 또한 단언문이 담당한다.

---
## 기본 타입 단언 (Primitive assertions)
- 단언문은 **가정**이나 **의도**를 명시해야 하며, **코드의 동작을 서술하는 문장**이어야 한다.
- 기본 타입 단언이란, *단언하려는 이유나 의도가 의미를 알 수 없는 단어나 숫자에 가려진 상황*을 말한다.

```java
  @Test
  void outputHasLineNumbers() {
    String content = "1st match on #1\nand\n2nd match on #3";
    String out = grep.grep("match", "test.txt", content);
    assertTrue(out.indexOf("test.txt:1  1st match") != 1);
    assertTrue(out.indexOf("test.txt:3  2st match") != 1);
  }
```
- 테스트란 **대상의 기능을 알려주는 구체적인 예제**여야만 한다.
- 그러나 위 예제의 단언문은 *너무 원시적*이라 확인하려는 게 정확히 무엇인지 분명치가 않다. 왜 하필 'test.txt:1'을 찾아야 하는지, -1과 비교는 왜 하는건지 등을 파악하는 데 시간이 걸린다.
- 기본 타입 단언의 핵심은 **단언 대상의 추상화 수준이 너무 낮다**는 점이다.
- 일단 `assertTrue`와 `!=` 비교문의 조합을 `assertThat`으로 바꿔본다.

```java
	@Test
	void outputHasLineNumbers() {
		String content = "1st match on #1\nand\n2nd match on #3";
		String out = grep.grep("match", "test.txt", content);
		assertThat(out.indexOf("test.txt:1  1st match"), is(not(-1)));
		assertThat(out.indexOf("test.txt:3  2st match"), is(not(-1)));
	}
```
- `assertThat`과 Hamcrest Matcher인 `is`와 `not`을 사용했더니, 단언문에서 index가 -1이 아니길 기대했다는 것을 좀 더 쉽게 알아차릴 수 있게 됐다.
- 그런데 결국 테스트는 해당 문자열 조각이 포함되어 있는지를 확인하는 것인데, 굳이 -1이라는 인덱스를 써야 할까? 좀 더 **추상화**해서 다음과 같이 바꿔본다.

```java
	@Test
	void outputHasLineNumbers() {
		String content = "1st match on #1\nand\n2nd match on #3";
		String out = grep.grep("match", "test.txt", content);
		assertThat(out.contains("test.txt:1  1st match"), equals(true));
		assertThat(out.contains("test.txt:3  2st match"), equals(true));
	}
```
- `String#contains`까지 적용했더니, 훨씬 의도가 명확하고 적절하게 표현되었다.
- 테스트 모드에서 어떤 방식으로 의도를 표현할지 결정할 때, ~~코드 중복~~이나 ~~성능~~보다 **가독성**과 **명료성**이 더 중요하다.
- 마지막으로, JUnitMatchers를 사용하여 의도를 더 명확하게 표현할 수 있다.

```java
	@Test
	void outputHasLineNumbers() {
		String content = "1st match on #1\nand\n2nd match on #3";
		String out = grep.grep("match", "test.txt", content);
		assertThat(out.containsString("test.txt:1  1st match"));
		assertThat(out.containsString("test.txt:3  2st match"));
	}
```
- 핵심은 **검사하려는 기능과 단언문을 같은 언어와 어휘로 표현**해야 한다는 것이다. 그래야 가독성이 좋아지고 명확하게 알아볼 수 있다.
- 테스트에서 !=나 == 등의 비교문을 사용하는 단언문이 있다면, **추상화 수준이 적절한지** 되짚어 보자. 0이나 -1 등의 매직 넘버도 물론이다. **단언문을 즉시 이해할 수 없다면 기본 타입 단언**에 해당하며, 리팩토링 대상일 가능성이 높다.

---
## 광역 단언 (Hyperassertions)


---
## 비트 단언 (Bitwise assertions)

---
## 부차적 상세정보 (Incidental details)

---
## 다중 인격 (Split personality)

---
## 쪼개진 논리 (Split logic)

---
## 매직 넘버 (Magic numbers)

---
## 셋업 설교 (Setup sermon)

---
## 과잉보호 테스트 (Overprotective tests)