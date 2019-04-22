# Mock 测试

> * **作者**：March
> * **链接**：[Mock 测试](https://github.com/maoqiqi/DevelopmentArms/blob/master/todoapp/JAVA_MOCK.md)
> * **邮箱**：fengqi.mao.march@gmail.com
> * **头条**：https://toutiao.io/u/425956/subjects
> * **简书**：https://www.jianshu.com/u/02f2491c607d
> * **掘金**：https://juejin.im/user/5b484473e51d45199940e2ae
> * **CSDN**：http://blog.csdn.net/u011810138
> * **SegmentFault**：https://segmentfault.com/u/maoqiqi
> * **StackOverFlow**：https://stackoverflow.com/users/8223522
>
> 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

## 目录

* [Mock概述](#Mock概述)
* [Mockito概述](#Mockito概述)
* [Mockito基本使用](#Mockito基本使用)
  * [验证](#验证)
  * [Stubbing](#Stubbing)
  * [doXxx等用法](#doXxx等用法)
  * [参数捕捉](#参数捕捉)
  * [重置](#重置)
  * [序列化](#序列化)
  * [注解](#注解)
* [Link](#Link)

## Mock概述

### 1.什么是 Mock 测试？

在软件开发的世界之外，"mock"一词是指模仿或者效仿。 因此可以将"mock"理解为一个替身，替代者。在软件开发中提及"mock"，通常理解为模拟对象或者Fake。

Mock测试就是在测试过程中，对于某些不容易构造或者不容易获取的对象，用一个虚拟的对象来创建以便测试的测试方法。

### 2.为什么需要 Mock 测试？

Mock 测试是为了解决units之间由于耦合而难于被测试的问题。

Mock 最大的功能是帮你把单元测试的耦合分解开，如果你的代码对另一个类或者接口有依赖，它能够帮你模拟这些依赖，并帮你验证所调用的依赖的行为。

### 3.Mock的好处是什么?

* 提前创建测试; TDD（测试驱动开发）
  > 这是个最大的好处吧。如果你创建了一个Mock那么你就可以在service接口创建之前写Service Tests了，
  这样你就能在开发过程中把测试添加到你的自动化测试环境中了。换句话说，模拟使你能够使用测试驱动开发。

* 团队可以并行工作
  > 这类似于上面的那点；为不存在的代码创建测试。但前面讲的是开发人员编写测试程序，这里说的是测试团队来创建。
  当还没有任何东西要测的时候测试团队如何来创建测试呢？模拟并针对模拟测试！
  这意味着当service接口需要测试时，实际上QA团队已经有了一套完整的测试组件；
  没有出现一个团队等待另一个团队完成的情况。这使得模拟的效益型尤为突出了。

* 你可以创建一个验证或者演示程序
  > 由于Mocks非常高效，Mocks可以用来创建一个概念证明，作为一个示意图，或者作为一个你正考虑构建项目的演示程序。
  这为你决定项目接下来是否要进行提供了有力的基础，但最重要的还是提供了实际的设计决策。

* 为无法访问的资源编写测试
  > 这个好处不属于实际效益的一种，而是作为一个必要时的“救生圈”。有没有遇到这样的情况？
  当你想要测试一个service接口，但service需要经过防火墙访问，防火墙不能为你打开或者你需要认证才能访问。
  遇到这样情况时，你可以在你能访问的地方使用MockService替代，这就是一个“救生圈”功能。

* Mock 可以交给用户
  > 在有些情况下，某种原因你需要允许一些外部来源访问你的测试系统，像合作伙伴或者客户。
  这些原因导致别人也可以访问你的敏感信息，而你或许只是想允许访问部分测试环境。
  在这种情况下，如何向合作伙伴或者客户提供一个测试系统来开发或者做测试呢？最简单的就是提供一个mock，无论是来自于你的网络或者客户的网络。
  soapUI mock非常容易配置，他可以运行在soapUI或者作为一个war包发布到你的java服务器里面。

* 隔离系统
  > 有时，你希望在没有系统其他部分的影响下测试系统单独的一部分。由于其他系统部分会给测试数据造成干扰，影响根据数据收集得到的测试结论。
  使用mock你可以移除掉除了需要测试部分的系统依赖的模拟。当隔离这些mocks后，mocks就变得非常简单可靠，快速可预见。
  这为你提供了一个移除了随机行为，有重复模式并且可以监控特殊系统的测试环境。


## Mockito概述

### 1.Mockito是什么？

Mockito 是一个强大的用于 Java 开发的Mock测试框架, 通过 Mockito 我们可以创建和配置 Mock 对象, 进而简化有外部依赖的类的测试.

### 2.Mockito资源

- 官网：http://mockito.org
- API文档：http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html
- 项目源码：https://github.com/mockito/mockito


## Mockito基本使用

### 验证

> 一旦创建,mock会记录所有交互,你可以验证所有你想要验证的东西.

```java
@Test
public void testVerify() throws Exception {

    /**
     * 1.1 验证行为
     */
    // 创建 mock 对象
    List mockList1 = mock(List.class);
    // 使用 mock 对象
    mockList1.add("once");
    mockList1.clear();
    // 验证是否调用过一次add("once")方法
    verify(mockList1).add("once");
    // 验证是否调用过一次clear()方法
    verify(mockList1).clear();

    /**
     * 1.2 验证函数的调用次数,最多、最少、从不等
     */
    List mockList2 = mock(List.class);
    mockList2.add("once");
    mockList2.add("twice");
    mockList2.add("twice");
    mockList2.add("three times");
    mockList2.add("three times");
    mockList2.add("three times");
    // 下面的两个验证函数的验证效果一样。因为verify默认验证的就是times(1)
    verify(mockList2).add("once");
    verify(mockList2, times(1)).add("once");
    // 验证具体的执行次数
    verify(mockList2, times(2)).add("twice");
    verify(mockList2, times(3)).add("three times");
    // 使用never()进行验证,never()相当于times(0)
    verify(mockList2, never()).add("never happened");
    // 使用atLeast()/atMost()来进行验证
    verify(mockList2, atLeastOnce()).add("three times");
    verify(mockList2, atLeast(2)).add("three times");
    verify(mockList2, atMost(5)).add("three times");

    /**
     * 1.3 确保交互操作没有执行在Mock对象上
     */
    List mockList5 = mock(List.class);
    // 删除注释,测试不通过
    // mockList5.add("");
    // 下面的两个验证函数的验证效果一样。
    verify(mockList5, never()).add("");
    // 没有发生交互
    verifyZeroInteractions(mockList5);

    /**
     * 1.4 查找是否有未验证的交互
     */
    List mockList6 = mock(List.class);
    mockList6.add("one");
    mockList6.add("two");
    verify(mockList6).add("one");
    verify(mockList6).add("two");
    // 注释上面这句代码,验证失败,因为mockList6.add("two")尚未验证
    verifyNoMoreInteractions(mockList6);

    /**
     * 1.5 按顺序来验证
     */
    // A. 单个mock对象它的方法必须按照顺序来调用。
    List singleMock = mock(List.class);
    // 使用单个mock对象
    singleMock.add("was added first");
    singleMock.add("was added second");
    // 为单个Mock创建一个InOrder的顺序验证
    InOrder inOrder = inOrder(singleMock);
    // 验证调用次数,若是调换两句,将会出错,因为singleMock.add("was added first")是先调用的
    inOrder.verify(singleMock).add("was added first");
    inOrder.verify(singleMock).add("was added second");

    // B. 多个mock也必须按照顺序来使用
    List firstMock = mock(List.class);
    List secondMock = mock(List.class);
    firstMock.add("was called first");
    secondMock.add("was called second");
    // 创建一个inOrder对象，把需要按照顺序验证的mock传递进去。
    inOrder = inOrder(firstMock, secondMock);
    // 保证firstMock在secondMock之前调用
    inOrder.verify(firstMock).add("was called first");
    inOrder.verify(secondMock).add("was called second");

    /**
     * 1.6 超时验证
     */
    Supplier mockList3 = mock(Supplier.class);
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                // 当事件修改为150的时候,下面的测试不通过
                Thread.sleep(50);
                mockList3.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }).start();
    // 测试程序将会在下面这句阻塞100毫秒,timeout的时候再进行验证是否执行过get()
    verify(mockList3, timeout(100)).get();
}
```

### Stubbing

```java
@Test
public void testStubbing() throws Exception {
    /**
     * 2.1 Subbing测试桩
     */
    LinkedList mockedList = mock(LinkedList.class);
    // 设置桩
    when(mockedList.get(0)).thenReturn("first");
    when(mockedList.get(1)).thenThrow(new RuntimeException());
    // 这里打印"first"
    System.out.println(mockedList.get(0));
    // 这里会抛 runtime exception
    // System.out.println(mockedList.get(1));
    // 这里会打印"null",因为get(999)没有设置
    System.out.println(mockedList.get(999));

    /**
     * 2.2 参数匹配
     */
    LinkedList mockedList2 = mock(LinkedList.class);
    // 使用内置的anyInt()参数匹配Stubbing,当调用get(整型值)时都返回"element"
    when(mockedList2.get(anyInt())).thenReturn("element");
    // 这里打印"element"
    System.out.println(mockedList2.get(999));
    // 你也可以验证参数匹配器
    verify(mockedList2).get(anyInt());

    // 警告：如果你使用了参数匹配器,那么所有参数都应该使用参数匹配器.

    // 正确的，因为eq返回参数匹配器
    // verify(mock).someMethod(anyInt(), anyString(), eq("third argument"));
    // 将会抛异常，因为第三个参数不是参数匹配器
    // verify(mock).someMethod(anyInt(), anyString(), "third argument");

    /**
     * 2.3 为连续的调用做测试桩（迭代式的测试桩）
     */
    Function mock = mock(Function.class);
    when(mock.apply("some arg")).thenReturn("one").thenReturn("two");
    // 第一次调用打印"one"
    System.out.println(mock.apply("some arg"));
    // 第二次调用打印"two"
    System.out.println(mock.apply("some arg"));
    // 后续继续调用,打印"two",以最后一个stub为准
    System.out.println(mock.apply("some arg"));

    // 下面是一个更简洁的写法
    when(mock.apply("some arg")).thenReturn("one", "two", "three");
    System.out.println(mock.apply("some arg"));
    System.out.println(mock.apply("some arg"));
    System.out.println(mock.apply("some arg"));

    // 注意:如果不是.thenReturn()连续调用,而是使用具有相同匹配器或参数的多个Stubbing,则每个Stubbing将覆盖前一个Stubbing
    when(mock.apply("some arg")).thenReturn("one");
    when(mock.apply("some arg")).thenReturn("two");
    System.out.println(mock.apply("some arg"));
    System.out.println(mock.apply("some arg"));

    /**
     * 2.4 为回调做测试桩
     */
    when(mock.apply("some arg")).thenAnswer(new Answer<String>() {
        @Override
        public String answer(InvocationOnMock invocation) throws Throwable {
            // 获得函数调用的参数
            Object[] args = invocation.getArguments();
            // 获得Mock对象本身
            Object mock = invocation.getMock();
            return "called with arguments: " + args[0];
        }
    });
    // 输出:"called with arguments: some arg"
    System.out.println(mock.apply("some arg"));

    /**
     * 2.5 为未stub的方法设置默认返回值（自1.7开始）
     */
    List listOne = mock(List.class, Mockito.RETURNS_SMART_NULLS);
    List listTwo = mock(List.class, new Answer() {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            return "default value";
        }
    });
    System.out.println(listOne.get(999));
    System.out.println(listTwo.get(999));

    /**
     * 2.6 spy监控真实对象
     */
    List list = new LinkedList();
    List spy = spy(list);

    // 可选的,你可以stub某些方法
    when(spy.size()).thenReturn(100);

    // 调用"真正"的方法
    spy.add("one");
    spy.add("two");

    // 打印"one"
    System.out.println(spy.get(0));
    // size()方法被stub了,打印"100"
    System.out.println(spy.size());

    // 可选,验证spy对象的行为
    verify(spy).add("one");
    verify(spy).add("two");

    // 理解监控真实对象非常重要！
    // 有时候，在监控对象上使用when(Object)来进行打桩是不可能或者不切实际的。
    // 因此在使用spy的时候，请考虑doReturn|Answer|Throw() 这一系列的方法来打桩。

    // 例如：因为当调用spy.get(10)时会调用真实对象的get(10)函数，此时会发生IndexOutOfBoundsException。
    // when(spy.get(10)).thenReturn("foo");
    // 你需要使用doReturn来进行打桩
    doReturn("foo").when(spy).get(10);
}
```

对于stubbing,有以下几点需要注意：

- 默认情况下，对于有返回值的所有方法，mock会默认返回null、空集合、默认值。比如，int/Integer返回0，boolean/Boolean返回false。
- stubbing可以被覆盖，但是请注意覆盖已有的stubbing有可能不是很好。
- 一旦stubbing，不管调用多少次，方法都会永远返回stubbing的值。
- 当你对同一个方法进行多次stubbing，最后一次stubbing是最重要的。

### doXxx等用法

doReturn()|doThrow()| doAnswer()|doNothing()|doCallRealMethod()等用法

```java
@Test
public void testDoXXX() throws Exception {
    List mockedList = mock(List.class);
    doThrow(new RuntimeException()).when(mockedList).clear();
    // 以下会抛异常
    mockedList.clear();
}
```

### 参数捕捉

```java
@Test
public void testCapturingArguments() throws Exception {
    List mockedList = mock(List.class);
    ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    mockedList.add("John");
    // 验证
    verify(mockedList).add(argument.capture());
    // 验证
    assertEquals("John", argument.getValue());
}
```

### 重置

```java
// 5. 重置（自1.8.0开始）
@Test
public void testReset() throws Exception {
    List mock = mock(List.class);
    when(mock.size()).thenReturn(10);
    System.out.println(mock.size());
    reset(mock);
    // 从这开始,之前的交互和stub将全部失效
    System.out.println(mock.size());
}
```

### 序列化

```java
@Test
public void testSerializableMocks() throws Exception {
    List serializableMock = mock(List.class, withSettings().serializable());
}
```

### 注解

```java
// 对于final类、匿名类和Java的基本类型是无法进行mock的。
@Mock
private List<String> list1 = new ArrayList<>();
@Mock
private List<String> list2;

/**
 * 新的注解：@Captor, @Spy, @InjectMocks（1.8.3）
 */

@Captor
private ArgumentCaptor<String> argumentCaptor;

@Spy
private LinkedList list3 = new LinkedList<>();
// 也可以这样写,mockito 会自动实例化
@Spy
private LinkedList list4;

@Mock
private UserService userService1;
// 如果此注解声明的变量需要用到mock对象,mockito会自动注入mock或spy成员
@InjectMocks
private UserService userService2;
@Spy
private UserService userService3;

@Before
public void initMocks() {
    // 必须,否则注解无效
    MockitoAnnotations.initMocks(this);
}

@Test
public void testAnnotations() throws Exception {
    System.out.println(list1.size());
    System.out.println(list2.size());
    System.out.println(list3.size());
    System.out.println(list4.size());

    list2.add("one");
    verify(list2).add(argumentCaptor.capture());

    // 不会打印login
    System.out.println(userService1.login());
    System.out.println("-------------------");
    // 打印login
    System.out.println(userService2.login());
    System.out.println("-------------------");
    System.out.println(userService3.login());
    System.out.println("-------------------");

    verify(userService1).login();
    // 报错
    // verify(userService2).login();

    when(userService1.login()).thenReturn("login success");
    System.out.println(userService1.login());

    // @Mock与@InjectMocks的区别
    // @Mock: 创建一个Mock，Mock对象只能调用stubbed方法，调用不了它真实的方法。
    // 但Mockito可以监视一个真实的对象，这时对它进行方法调用时它将调用真实的方法，
    // 同时也可以stubbing这个对象的方法让它返回我们的期望值。
    // @InjectMocks: 创建一个实例，简单的说可以调用真实代码的方法.
}

class UserService {

    public String login() {
        System.out.println("login()");
        return "UserService->login()";
    }
}
```


## Link

* [Espresso UI 测试框架](ANDROID_ESPRESSO.md)
* [安卓架构组件 Room 持久化类库](ANDROID_ROOM.md)