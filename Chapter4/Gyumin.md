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
- 검사하려는 동작의 _아주 작은 하나까지도 놓치지 않으려는_ 집착의 산물
- **너무 광대한 범위를 비교**하는 것이 문제
- 결과적으로, **결과에 작은 변화만 생겨도 테스트가 바로 실패**하고, **본래 의도했던 것도 광활한 검증 범위에 묻혀** 희석된다.

> 테스트가 실패하는 이유는 오직 하나뿐이어야 한다.  
  (SRP의 변형)

- 항상 실패하는 테스트는 있으나 마나다. 그런데 광역 단언은 **너무 쉽게 실패하는** 테스트를 만들어 낸다. 테스트가 실패한다는 건 어떤 부분이 잘못됐는지 잡아낼 수 있어야한다는 것인데, 실패하는 이유가 여러가지라면 그럴 수 없을 것이다.
- 즉 테스트가 실패하면 **왜** 실패했는지가 우리의 관심사다. 하지만 광역 단언에서는, **어디가 바뀌었고 실패의 원인은 무엇인지 파악하려면 매번 세부 내용까지 분석**해봐야 한다.
- 뿐만 아니라 광역 단언은 **프로그래머가 테스트의 의도와 핵심을 파악하는 데도 방해**가 된다.

### 개선 방법
- 본질과 관련 없는 세부 내용을 찾아 테스트에서 제거하자.
- 일단 테스트를 나눈다(쪼갠다).
- 리팩토링을 통해, 각각의 테스트는 관련 없는 세부 정보는 숨기고 **한 가지 일에만 충실**한 모습이 된다. (적어도 단위 테스트에서는) ~~한 번에 전체를 보기~~보다는, **핵심을 빠르게 파악할 수 있는 테스트**를 만드는 것이 낫다. 그래야만 **실패했을 때 문제의 근본 원인을 빠르게 찾을 수** 있다.

---
## 비트 단언 (Bitwise assertions)
- 기본 타입 단언의 특수한 형태
```java
public class PlatformTest {
  @Test
  void platformBitLength() {
    assertThat(Platform.IS_32_BIT ^ Platform.IS_64_BIT).isTrue();
  }
}
```
- 위의 단언문에서는 두 이진값에 비트 연산인 ^를 수행한 결과가 참(true)이라 말하고 있다.
- 그런데 본 테스트는 비트나 바이트 같은 저수준 개념이 아니라, "지금 실행되는 플랫폼이 32비트 시스템인가, 64비트 시스템인가?"라는 고수준의 논의를 원하는 것이다. 그러나 낯선 비트 연산자 때문에 테스트의 존재 이유가 가려져 있다.
- **단언문의 최적화는 우리가 할 일이 아니다**. 즉, 성능보다는 가독성이 중요한 것이다. (= **최적화 때문에 비트 연산자를 쓸 이유도 없다**.)

### 개선 방법
- 비트 연산자를 **부울 연산자(Boolean operator)**로 교체해서, **기대하는 결과를 하나씩 명확하게 표현**한다.
```java
public class PlatformTest {
  @Test
  void platformBitLength() {
    assertTrue("32비트 또는 64비트 플랫폼이 아닙니까?", Platform.IS_32_BIT || Platform.IS_64_BIT);
    assertFalse("동시에 32비트이면서 64비트일 수는 없습니다.", Platform.IS_32_BIT && Platform.IS_64_BIT);
  }
}
```
- 내용이 조금 길어지기는 했지만, **단언하려는 의도는 더 명확해졌다**.
- **고수준 개념은 그에 합당한 고차원적인 언어로 표현**하자.

---
## 부차적 상세정보 (Incidental details)
- 테스트 코드에 **부수적인 정보**가 넘쳐 흐를 때

(부차적 상세정보 예제)

### 개선 방법
1. 핵심이 아닌 설정은 private 메서드나 setUp 메서드로 추출하라.  
  (테스트의 핵심이 아닌) **설정과 세부 정보**는 모두 테스트 메서드 밖으로 내보낸다.
2. (클래스, 메서드, 필드, 변수에) 적절하고(appropriate) 서술적인(descriptive) 이름을 사용하라.  
3. 한 메서드 안에서는 모두 같은 수준으로 추상화하라.  
  추상화 수준을 일치시키면 가독성이 개선된다.

---
## 다중 인격 (Split personality)
- 하나의 테스트가 여러 인격(관심사)을 갖고 있는 것. 하나의 테스트가 여러 개의 테스트를 포함하고 있는 것.
- **하나의 테스트는 오직 한 가지만 똑바로 검사해야 한다**.
- 다중 인격은 **여러 논점을 하나의 테스트에 뒤섞어서, 테스트의 세부 정보와 큰 그림 모두를 감춰버린다**.
- 다중 인격인지를 살펴보는 것은 가장 손쉬운 테스트 개선 방법 중 하나다.

```java
public class TestConfiguration {
	private Configuration c;
	
	@BeforeAll
	void instantiateDefaultConfiguration() {
		c = new Configuration();
	}

	@Test
	void CommandLine_인자_파싱_테스트() {
		String args[] = { "-f", "hello.txt", "-v", "--version" };
		c.processArguments(args);
		
		assertThat(c.getFileName()).isEqualTo("hello.txt");
		assertThat(c.isDebuggingEnabled()).isFalse();
		assertThat(c.isWarningEnabled()).isFalse();
		assertThat(c.isVerbose()).isTrue();
		assertThat(c.shouldShowVersion()).isTrue();

    c = new Configuration();
		try {
			c.processArguments(new String[] {"-f"});
			fail("Should've failed");
		} catch (InvalidArgumentException expected) {
			// this is okay and expected
		}
	}
}
```
- 위 테스트에는 `c.processArguments()` 2개와 단언문 6개가 포함돼있다. 최소한 2개의 시나리오, 즉 2개의 서로 다른 테스트 인격이 있음을 의미한다. 각각의 테스트로 분리해본다.

```java
public class TestConfiguration {
	private Configuration c;

	@BeforeAll
	void instantiateDefaultConfiguration() {
		c = new Configuration();
	}

	@Test
	void 유효한_인자가_제공됨() {
		String args[] = { "-f", "hello.txt", "-v", "--version" };
		c.processArguments(args);

		assertThat(c.getFileName()).isEqualTo("hello.txt");
		assertThat(c.isDebuggingEnabled()).isFalse();
		assertThat(c.isWarningEnabled()).isFalse();
		assertThat(c.isVerbose()).isTrue();
		assertThat(c.shouldShowVersion()).isTrue();
	}

	@Test
	void 인자가_빠져있음() {
		assertThrows(InvalidArgumentException.class, () -> {
			c.processArguments(new String[] {"-f"});
		});
	}
}
```
- 큰 시나리오를 떼어 냈더니, 각 테스트 메서드에 서술적인 이름을 지어주기가 훨씬 수월해졌다. (이전 예제의 `CommandLine_인자_파싱_테스트()`보다 훨씬 서술적이다.)
- 첫 테스트의 단언처럼 명시적으로 몇가지 유효한 인자를 전달받는 경우도 있지만, 인자가 없는 기본값일 수도 있다. 이 문제를 **여러 개의 테스트 클래스로 나눠서** 해결해본다.

```java
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractConfigTestCase {
	protected Configuration c;

	@BeforeAll
	void instantiateDefaultConfiguration() {
		c = new Configuration();
		c.processArguments(args());
	}

	protected String[] args() {
		return new String[] {};
	}
}

public class TestDefaultConfigValues extends AbstractConfigTestCase {
	@Test
	void 기본_옵션이_정상적으로_셋팅됨() {
		assertThat(c.isDebuggingEnabled()).isFalse();
		assertThat(c.isWarningEnabled()).isFalse();
		assertThat(c.isVerbose()).isFalse();
		assertThat(c.shouldShowVersion()).isFalse();
	}
}

public class TestExplicitlySetConfigValues extends AbstractConfigTestCase {
	@Override
	protected String[] args() {
		return new String[] {"-f", "hello.txt", "-v", "-d", "-w", "--version"};
	}

	@Test
	void 명시적으로_값_지정_옵션이_정상적으로_셋팅됨() {
		assertThat(c.getFileName()).isEqualTo("hello.txt");
		assertThat(c.isDebuggingEnabled()).isTrue();
		assertThat(c.isWarningEnabled()).isTrue();
		assertThat(c.isVerbose()).isTrue();
		assertThat(c.shouldShowVersion()).isTrue();
	}
}

public class TestConfigurationErrors extends AbstractConfigTestCase {
	@Override
	protected String[] args() {
		return new String[] {"-f"};
	}

	@Test
	void 인자가_빠져있으면_에러를_발생시킴() {
		assertThrows(InvalidArgumentException.class, () -> {
			
		});
	}
}
```
- 위의 리팩토링을 통해, **테스트 클래스 각각이 하나의 주제에만 충실**해졌다.
- 테스트 메서드 단위로 쪼개는 것이 나을까? 아니면 상속 등을 사용해서 클래스 단위로 나누는 것이 좋을까?
  - 기반 클래스를 통해 공유해야 할 **불변 객체가 거의 없고**, 테스트 메서드와 그에 딸린 **픽스처를 분리하는 것만으로 충분**하다면, 그것들만 따로 떼어 **독립된 테스트 클래스로** 만드는 것도 좋다.
  - 그렇게 하면 **복잡한 계층 구조와 그로 인한 '쪼개진 논리' 냄새를 피할 수 있다**.
  - 마치 영어로 쓰인 단편 소설(메서드 단위)보다는, 한글로 쓰인 장편 소설(클래스 단위)을 읽는 것이 빠른 것과 같다.
- 테스트의 여러 인격을 테스트 클래스 단위로 분리하면 좋은 점
  - 테스트의 의미가 드러나 이해하기 쉬워진다.
  - 나중에 코드를 수정하기 쉬워진다.
  - 코드가 뭘 하는 것인지, 어떤 시나리오가 빠져 있는지도 쉽게 파악할 수 있다.
  - 실수를 했을 때 잘못된 곳을 더 정확하게 집어준다.
- 광역 단언과의 차이는 무엇이며, 개선 방법에는 어떤 차이가 있는가.

---
## 쪼개진 논리 (Split logic)

---
## 매직 넘버 (Magic numbers)

---
## 셋업 설교 (Setup sermon)

---
## 과잉보호 테스트 (Overprotective tests)