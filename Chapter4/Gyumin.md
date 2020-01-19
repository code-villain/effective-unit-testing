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
1. 핵심이 아닌 설정은 **private 메서드나 setUp 메서드로 추출**하라(Helper Method).  
  (테스트의 핵심이 아닌) **설정과 세부 정보**는 모두 테스트 메서드 밖으로 내보낸다.
2. (클래스, 메서드, 필드, 변수에) 적절하고(appropriate) **서술적인(descriptive) 이름**을 사용하라.  
3. 한 메서드 안에서는 모두 **같은 수준으로 추상화**하라(켄트 벡 <구현 패턴> _대칭성_ 참조).  
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
  _☞ **픽스처(fixture)**: 테스트가 실행할 어떤 것(context). 시스템 속성, 테스트 클래스에 정의된 상수, 셋업 메서드가 초기화한 private 멤버 등_
  - 그렇게 하면 **복잡한 계층 구조와 그로 인한 '쪼개진 논리' 냄새를 피할 수 있다**.
  - 마치 영어로 쓰인 단편 소설(메서드 단위)보다는, 한글로 쓰인 장편 소설(클래스 단위)을 읽는 것이 빠른 것과 같다.
- 테스트의 여러 인격을 테스트 클래스 단위로 분리하면 좋은 점
  - 테스트의 의미가 드러나 이해하기 쉬워진다.
  - 나중에 코드를 수정하기 쉬워진다.
  - 코드가 뭘 하는 것인지, 어떤 시나리오가 빠져 있는지도 쉽게 파악할 수 있다.
  - 실수를 했을 때 잘못된 곳을 더 정확하게 집어준다.

---
## 쪼개진 논리 (Split logic)
- 여러 곳으로 흩어진 테스트 코드는 인지 과부하를 과중시키고, 테스트의 의미와 의도를 파악하기 어렵게 만든다.
- 무작정 쪼개는 건 곤란하다. 같은 속성을 공유하는 의미 있는 조각이 어디까지인지 신경 쓰면서 나눠야 한다.

```java
public class TestRuby {
  private Ruby runtime;

  @Before
  void setUp() throws Exception {
    runtime = Ruby.newInstance();
  }

  @Test
  void testVarAndMet() throws Exception {
    runtime.getLoadService().init(new ArrayList());
    eval("load 'test/testVariableAndMethod.rb'");
    assertThat(eval("puts($a)")).isEqualTo("Hello World");
    assertThat(eval("puts $b")).isEqualTo("dlroW olleH");
    assertThat(eval("puts $d.reverse, $c, $e.reverse")).isEqualTo("Hello World");
    assertThat(eval("puts $f, \" \", $g, \" \", $h")).isEqualTo("135 20 3");
  }
}
```
- 위 테스트의 흩어짐 정도는 심각하다. 왜 puts $b가 "Hello World"의 역순을 반환해야 하는지 알 수가 없는데, 이는 testVariableAndMethod.rb라는 다른 **외부 데이터(파일)에 정보가 흩어져** 있기 때문이다.  
(_testVariableAndMethod.rb 파일에는 a = String.new("Hello World") b = a.reverse 등의 변수 할당문들이 나열돼 있다._)
- 이렇게 쪼개진 논리를 해결하는 가장 간단한 방법은, **필요한 외부 데이터와 코드를 모두 테스트 안으로 옮기는** 것이다.

```java
@Test
void testVarAndMet() throws Exception {
  runtime.getLoadService().init(new ArrayList());

  AppendableFile script = withTempFile();
  script.line("a = String.new('Hello World')");
  script.line("b = a.reverse");
  script.line("c = ' '");
  script.line("d = 'Hello'.reverse");
  ...
  // (testVariableAndMethod.rb 내에 있는 내용 합치기)

  eval("load '" + script.getAbsolutePath() + "'");
  assertThat(eval("puts($a)")).isEqualTo("Hello World");
  assertThat(eval("puts $b")).isEqualTo("dlroW olleH");
  assertThat(eval("puts $d.reverse, $c, $e.reverse")).isEqualTo("Hello World");
  assertThat(eval("puts $f, \" \", $g, \" \", $h")).isEqualTo("135 20 3");
}
```
- 흩어졌던 정보를 테스트 메서드 안으로 합쳐서 '쪼개진 논리' 냄새를 해결했지만, 다중 인격과 셋업 설교 등의 다른 냄새를 풍긴다. **테스트를 둘로 나눠 다중 인격을 해결**해보자.
- 쪼개진 논리 → 외부의 데이터와 코드를 모두 테스트 메서드 내부로 일단 옮기기 → 다중 인격 발생 → 여러 테스트 메서드로 나눠서 다중 인격 해결

```java
@Before
void setUp() throws Exception {
  runtime.getLoadService().init(new ArrayList());
  script = withTempFile();
}

@Test
void 변수_할당_테스트() throws Exception {
  script.line("a = String.new('Hello')");
  script.line("b = 'World'");
  script.line("c = 1 + 2");
  afterEvaluating(script);
  assertThat(eval("puts(a)")).isEqualTo("Hello");
  assertThat(eval("puts b")).isEqualTo("World");
  assertThat(eval("puts $c")).isEqualTo("3");
}

@Test
void 메서드_호출_테스트() throws Exception {
  script.line("a = 'Hello'.reverse");
  script.line("b = 'Hello'.length()");
  script.line("c = ' abc '.trim(' ', '_')");
  afterEvaluating(script);
  assertThat(eval("puts a")).isEqualTo("olleH");
  assertThat(eval("puts b")).isEqualTo("3");
  assertThat(eval("puts c")).isEqualTo("_abc_");
}

private void afterEvaluating(AppendableFile sourceFile) throws Exception {
  eval("load '" + sourceFile.getAbsolutePath() + "'");
}
```
- 쪼개진 논리나 데이터를 독립 파일로 두는 것보다는, 그것을 사용하는 **테스트 메서드 안에** 두는 방법이 (일반적으로) 바람직하다. 쪼개진 논리를 수용하는 건 최후의 보루여야 한다.
- _(p.113~114에 "데이터나 로직을 언제 통합해야 할까?"라는 내용이 있는데, 세부적인 내용은 잘 이해가 되지 않으나 결국 외부 데이터가 짧다면 통합하되 어려울 경우엔 독립 파일로 남겨 두라는 것이다. 다만 가벼이 여길 일은 아니므로 몇 가지 지침을 지켜야 한다.)_

---
## 매직 넘버 (Magic numbers)
- 매직 넘버: 소스코드 중 **할당문이나 메서드 호출 등에 박혀 있는 숫자로 된 값**
- 매직 넘버는 **뜻을 알 수 없기 때문에** 피해야 한다.
- 따라서 매직 넘버를 **의미가 분명한 이름의 상수나 변수로 대체해서 읽기 쉬운 코드로** 만들어야 한다.
```java
public class BowlingGameTest {
  @Test
  void perfectGame() throws Exception {
    roll(10, 12); // 10과 12는 무슨 뜻인가
    assertThat(game.score()).isEqualTo(300);  // 결과는 왜 300이어야 하는가
  }
}
```
- 위 예제에서 10, 12, 300은 모두 매직 넘버에 해당한다.
- 매직 넘버를 개선하는 방법은 크게 2가지가 있다.
  - 첫번째는 **정적 상수나 지역 변수**로 바꿔주는 것이다. 이를테면 10을 `TEN_PINS`, 12를 `TWELVE_TIMES`와 같은 상수로 만들어주는 것이다. 가장 보편적인 방법이다.
  - 두번째는 **메서드 방식**인데, 아래와 같다.

```java
public class BowlingGameTest {
  @Test
  void perfectGame() throws Exception {
    roll(pins(10), times(12));
    assertThat(game.score()).isEqualTo(300);
  }

  private int pins(int n) { return n; }
  private int times(int n) { return n; }
}
```
- 메서드 방식이 유리해지려면 매개변수에 들어갈 여러 개의 값에 대해 여러 개의 테스트를 만들어야 하겠다.

---
## 셋업 설교 (Setup sermon)
- 셋업 '설교'란, **짧은 테스트를 위한 너무 긴 준비작업(setUp)**을 뜻한다.
- 셋업 역시 테스트의 일부이기 때문에, **셋업이 복잡해지면 자연스럽게 테스트의 복잡도도 함께 커진다**.
- **테스트를 온전히 이해하려면, 테스트가 사용하는 픽스처(fixture)도 이해해야 한다**. 즉 **픽스처를 이해하지 못하면 테스트의 목적 역시 온전히 이해할 수 없다**.  
_☞ 픽스처(fixture): ['다중 인격'](https://github.com/code-villain/effective-unit-testing/blob/master/Chapter4/Gyumin.md#%EB%8B%A4%EC%A4%91-%EC%9D%B8%EA%B2%A9-split-personality) 내용 참조_
- 셋업 설교는 사실 **부차적 상세정보 냄새의 유형 중 하나**다. 따라서 개선 방법 역시 동일하다.
  1. 셋업에서 **핵심을 제외한 상세 정보는 private 메서드로 추출**한다.
  2. 알맞은 **서술적 이름**을 사용한다.
  3. 셋업 내의 **추상화 수준을 통일**한다.

---
## 과잉보호 테스트 (Overprotective tests)
- 테스트의 실제(진짜배기) 단언문에 도달하기 전까지 불필요한 중간 단계 단언문이 많이 등장하는 것
- 어차피 실패할 텐데도, 실패에 대한 강박관념에 사로잡혀 가치 없는 테스트를 만드는 경우
- **아무런 가치도 보태주지 않으므로 제거되어야 한다**.
```java
@Test
void count() {
  Data data = project.getData();
  assertNotNull(data);
  assertEquals(4, data.count());
}
```

- 위 예제가 **과잉보호 테스트**인 이유는,
  - 첫번째 단언문인 `assertNotNull`은 사실 필요하지 않기 때문이다.
  - `data`의 메서드를 호출하기에 앞서 `data`가 null인지 확인하기 때문이다(NPE로부터 자신을 보호하고자 하는 프로그래머의 방어적 코드).
  - `data`가 null이라면, `assertNotNull`이 있으나 없으나 어차피 테스트는 실패하기 때문이다.
- 불필요한 `assertNotNull` 같은 단언문을 굳이 사용하는 이유는 보통의 경우, *디버깅을 쉽게 하기 위함*인 경우가 많다. 즉 JUnit이 출력한 **깔끔한** 스택 추적 메시지를 보겠다는 것이다.
  - 하지만, 어차피 NullPointerException의 스택 추적을 클릭하면 null을 참조하려 했던 위치로 정확하게 이동할 수 있다.
  - 따라서 불필요한 **과잉보호 테스트**를 추가하는 것은 딱히 좋은 점도 없을 뿐더러, **테스트를 불명확하고 혼란스럽게 만들 뿐**이다.
- 다만 `data.getSummary().getTotal()`과 같은 코드처럼 *메서드를 연쇄적으로 호출하는 경우*에는, NPE가 어떤 메서드를 호출할 때 발생했는지 곧바로 확인하기 위해 중간에 확인 코드를 삽입하는 경우가 있다.
  - 하지만 불필요한 단언문을 테스트 코드에 갖고 있기보다는, 어쩌다 테스트 코드가 실패했을 때 디버거를 통해서 한 단계씩 추적하거나, 임시로 assertNotNull을 추가해서 다시 테스트를 실행한 뒤 테스트가 끝나면 다시 원래대로 삭제하는 편이 낫다.
  - 즉 **매번 어수선한 테스트 코드를 읽는 것보다는, 테스트가 실패했을 때만 한 번씩 조금 더 수고하는 게 차라리 낫다**.