# Data Binding Library

数据绑定库是一个支持库，允许您使用声明格式(而不是编程方式)将布局中的UI组件绑定到应用程序中的数据源。

## 目录

* [概览](#概览)
* [基本使用](#基本使用)
  * [搭建环境](#搭建环境)
  * [创建数据绑定布局文件](#创建数据绑定布局文件)
  * [数据对象](#数据对象)
  * [绑定数据对象](#绑定数据对象)
* [布局表达式](#布局表达式)
  * [表达语言](#表达语言)
  * [事件处理](#事件处理)
    * [方法引用](#方法引用)
    * [侦听器绑定](#侦听器绑定)
  * [导入和变量和包含](#导入和变量和包含)
    * [导入](#导入)
    * [变量](#变量)
    * [包含](#包含)
* [使用可观察的数据对象](#使用可观察的数据对象)
  * [可观察对象](#可观察对象)
  * [可观察的字段](#可观察的字段)
  * [可观察的集合](#可观察的集合)
* [生成的绑定类](#生成的绑定类)
  * [创建绑定对象](#创建绑定对象)
  * [ViewStubs](#ViewStubs)
  * [立即绑定](#立即绑定)
  * [动态变量](#动态变量)
  * [自定义绑定类名称](#自定义绑定类名称)
* [绑定适配器](#绑定适配器)
  * [设置属性值](#设置属性值)
    * [自动方法选择](#自动方法选择)
    * [指定自定义方法名称](#指定自定义方法名称)
    * [提供自定义逻辑](#提供自定义逻辑)
  * [对象转换](#对象转换)
    * [自动对象转换](#自动对象转换)
    * [自定义转化](#自定义转化)
* [将布局视图绑定到体系结构组件](#将布局视图绑定到体系结构组件)
  * [使用LiveData通知UI有关数据更改的信息](#使用LiveData通知UI有关数据更改的信息)
  * [使用ViewModel管理与UI相关的数据](#使用ViewModel管理与UI相关的数据)
  * [使用ObservableViewModel可以更好地控制绑定适配器](#使用ObservableViewModel可以更好地控制绑定适配器)
* [双向数据绑定](#双向数据绑定)
  * [使用自定义属性进行双向数据绑定](#使用自定义属性进行双向数据绑定)
  * [转换器](#转换器)
  * [使用双向数据绑定的无限循环](#使用双向数据绑定的无限循环)


## 概览

传统布局通常在活动中定义，其中包含调用UI框架方法的代码。例如，下面的代码调用findViewById()来查找TextView小部件并将其绑定到viewModel变量的userName属性:

```
TextView textView = findViewById(R.id.tv);
textView.setText(viewModel.getUserName());
```

下面的示例展示了如何使用数据绑定库在布局文件中直接向小部件分配文本。这样就不需要调用上面显示的任何Java代码。注意在赋值表达式中使用`@{}`语法:

```
<TextView
    android:text="@{viewModel.userName}" />
```

在布局文件中绑定组件允许您删除活动中的许多UI框架调用，从而使它们更简单、更易于维护。这还可以提高应用程序的性能，并有助于防止内存泄漏和空指针异常。


## 基本使用

### 搭建环境

数据绑定库提供了灵活性和广泛的兼容性，所以您可以在运行Android 4.0 (API级别14)或更高的设备上使用它。

建议在您的项目中使用最新的Android插件Gradle。但是，1.5.0或更高版本支持数据绑定。

要将应用程序配置为使用数据绑定，请将dataBinding元素添加到项目模块中的gradle文件的构建中。如下例所示:

```
android {
...
  dataBinding {
     enabled true
  }
}
```

> 注意：您必须为依赖于使用数据绑定的库的应用程序模块配置数据绑定，即使应用程序模块不直接使用数据绑定也是如此。

### 创建数据绑定布局文件

数据绑定布局文件略有不同，首先是布局的根标记，然后是数据元素和视图根元素。这个视图元素是非绑定布局文件中的根元素。下面的代码显示了一个布局文件示例:

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="user"
            type="com.example.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.firstName}" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{user.lastName}" />
    </LinearLayout>
</layout>
```

> **注意**：布局表达式应该保持小而简单，因为它们不能进行单元测试，并且只有有限的IDE支持。为了简化[布局表达式](#布局表达式)，您可以使用自定义[绑定适配器](#绑定适配器)。

Android Studio 提供了一种自动将普通布局文件转换成数据绑定布局文件的方法。打开布局文件，然后`ALT + ENTER`，选择菜单`Convert to data binding layout`。

![data_binding](images/data_binding.png)

### 数据对象

我们现在假设你有一个普通的对象来描述User实体：

```
public class User {
    public final String firstName;
    public final String lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
```

这种类型的对象具有永不更改的数据。在应用程序中，数据通常只读取一次，之后就不再更改。也可以使用遵循一组约定的对象，例如在Java中使用访问器方法，如下例所示:

```
public class User {
    private final String firstName;
    private final String lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
}
```

从数据绑定的角度来看，这两个类是等价的。@{user.firstName}用于android:text属性，访问前一个类中的firstName字段，后一个类中的getFirstName()方法。
另外，如果方法存在，它也被解析为firstName()。

### 绑定数据对象

数据绑定库为每个布局文件生成一个绑定类。默认情况下，类的名称基于布局文件的名称。例如布局文件名是activity_main.xml，因此生成的相应类是ActivityMainBinding。
该类包含从布局属性(例如，user变量)到布局视图的所有绑定，并且知道如何为绑定表达式赋值。创建绑定的推荐方法是在扩展布局的同时进行绑定，如下面的示例所示:

```
@Override
protected void onCreate(Bundle savedInstanceState) {
   super.onCreate(savedInstanceState);
   ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
   User user = new User("Test", "User");
   binding.setUser(user);
}
```

在运行时，应用程序在UI中显示Test用户。或者，您可以使用LayoutInflater获得视图，如下面的示例所示:

```
ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
```


## 布局表达式

表达式语言允许您编写将变量连接到布局中的视图的表达式。数据绑定库自动生成将布局中的视图与数据对象绑定所需的类。库提供了诸如导入、变量以及可以在布局中使用的一些特性。下面是一些例子:

```
// Some examples of complex layout expressions
android:text="@{String.valueOf(index + 1)}"
android:visibility="@{age < 13 ? View.GONE : View.VISIBLE}"
android:transitionName='@{"image_" + id}'
```

### 表达语言

* 属性引用

  表达式可以引用类中的属性，方法如下:

  ```
  android:text="@{user.lastName}"
  ```

  > **注意**：生成的数据绑定代码自动检查空值并避免空指针异常。例如，在表达式@{user.lastName}中，如果user为null，则将user.lastName的默认值赋为null。
  如果您引用user.age，其中age是类型int，那么数据绑定使用默认值0。

  在布局编辑器中的预览窗格显示数据绑定表达式的默认值。

  ```
  android:text="@{user.lastName, default=@string/name}"
  ```

  如果只需要在项目的设计阶段显示默认值，可以使用tools属性而不是默认表达式值。

* 空合并操作符

  空合并操作符(??)在左操作数不为空时选择左操作数。

  ```
  android:text="@{user.displayName ?? user.lastName}"
  ```

  等价于

  ```
  android:text="@{user.displayName != null ? user.displayName : user.lastName}"
  ```

* 集合

  为了方便起见，可以使用[]操作符访问常见的集合，如arrays, lists, sparse lists, and maps。

  ```
  <data>
      <import type="java.util.List"/>
      <import type="android.util.SparseArray"/>
      <import type="java.util.Map"/>
      <variable name="list" type="List&lt;String>"/>
      <variable name="sparse" type="SparseArray&lt;String>"/>
      <variable name="map" type="Map&lt;String, String>"/>
      <variable name="index" type="int"/>
      <variable name="key" type="String"/>
  </data>
  …
  android:text="@{list[index]}"
  …
  android:text="@{sparse[index]}"
  …
  android:text="@{map[key]}"
  ```

  > **注意**：要使XML在语法上正确，必须转义`<`字符。例如:List<String>您必须编写List&lt;String>。

  您还可以使用对象引用映射中的值`object.key`。例如，上面例子中的@{map[key]}可以替换为@{map.key}。

* 字符串文字

  您可以使用单引号括起属性值，这允许您在表达式中使用双引号，如以下示例所示：

  ```
  android:text='@{map["firstName"]}'
  ```

  也可以使用双引号来包围属性值。这样做时，字符串文字应该用``（后引号）括起来：

  ```
  android:text="@{map[`firstName`]}"
  ```

* 资源

  您可以使用以下语法访问表达式中的资源：

  ```
  android:padding="@{large? @dimen/largePadding : @dimen/smallPadding}"
  ```

  格式字符串和复数可以通过提供参数来计算:

  ```
  android:text="@{@string/nameFormat(firstName, lastName)}"
  ```

  > **注意**：当一个复数接受多个参数时，应传递所有参数。


### 事件处理

数据绑定允许您编写从视图分派的表达式处理事件（例如，onClick()方法）。事件属性名称由侦听器方法的名称确定，但有一些例外。
例如，View.OnClickListener有一个方法onClick()，所以这个事件的属性是`android:onClick`。

对于click事件，有一些特殊的事件处理程序需要使用android:onClick以外的属性来避免冲突。您可以使用以下属性来避免这些类型的冲突:

| Class | Listener setter | Attribute |
|:---|:---|:---|
|SearchView	  |setOnSearchClickListener(View.OnClickListener) |android:onSearchClick|
|ZoomControls |setOnZoomInClickListener(View.OnClickListener) |android:onZoomIn     |
|ZoomControls |setOnZoomOutClickListener(View.OnClickListener)|android:onZoomOut    |

您可以使用以下机制来处理事件:

* 方法引用:在表达式中，可以引用符合侦听器方法签名的方法。当表达式计算为方法引用时，数据绑定将方法引用和所有者对象包装在侦听器中，并在目标视图上设置该侦听器。
  如果表达式的计算结果为null，则数据绑定不会创建侦听器，而是设置一个空侦听器。
* 侦听器绑定:这些是在事件发生时计算的lambda表达式。数据绑定总是创建一个侦听器，并将其设置在视图上。当事件被分派时，侦听器计算lambda表达式。

#### 方法引用

事件可以直接绑定到处理程序方法，类似于`android:onClick`可以分配给活动中的方法的方式。
与View onClick属性相比的一个主要优点是表达式在编译时处理，因此如果该方法不存在或其签名不正确，则会收到编译时错误。

方法引用和侦听器绑定之间的主要区别在于，实际的侦听器实现是在绑定数据时创建的，而不是在触发事件时创建的。如果希望在事件发生时计算表达式，则应该使用侦听器绑定。

要将事件分配给其处理程序，请使用普通绑定表达式，其值为要调用的方法名称。例如，请考虑以下示例布局数据对象：

```
public class MyHandlers {
    public void onClickFriend(View view) { ... }
}
```

绑定表达式可以将视图的click listener分配给onClickFriend()方法，如下所示:

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="handlers"
            type="com.example.MyHandlers" />

        <variable
            name="user"
            type="com.example.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:onClick="@{handlers::onClickFriend}"
            android:text="@{user.firstName}" />
    </LinearLayout>
</layout>
```

> **注意**：表达式中方法的签名必须与侦听器对象中方法的签名完全匹配。

#### 侦听器绑定

侦听器绑定是在事件发生时运行的绑定表达式。它们与方法引用类似，但它们允许您运行任意数据绑定表达式。此功能适用于Gradle版本2.0及更高版本的Android Gradle插件。

在方法引用中，方法的参数必须与事件侦听器的参数匹配。在侦听器绑定中，只有您的返回值必须与侦听器的预期返回值匹配（除非它期望无效）。
例如，请考虑以下具有该`onSaveClick()`方法的演示者类：

```
public class Presenter {
    public void onSaveClick(Task task){}
}
```

然后，您可以将click事件绑定到onSaveClick()方法，如下所示：

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="task"
            type="com.android.example.Task" />

        <variable
            name="presenter"
            type="com.android.example.Presenter" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <Button
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:onClick="@{() -> presenter.onSaveClick(task)}" />
    </LinearLayout>
</layout>
```

当在表达式中使用回调函数时，数据绑定将自动创建必要的侦听器并为事件注册它。当视图触发事件时，数据绑定计算给定的表达式。
与常规绑定表达式一样，在计算这些侦听器表达式时，仍然会得到null和数据绑定的线程安全性。

在上面的例子中，我们没有定义传递给onClick(view)的视图参数。侦听器绑定为侦听器参数提供了两种选择:您可以忽略方法的所有参数，也可以为它们命名。
如果喜欢为参数命名，可以在表达式中使用它们。例如，上面的表达式可以写成:

```
android:onClick="@{(view) -> presenter.onSaveClick(task)}"
```

或者如果要在表达式中使用该参数，则可以按如下方式工作：

```
public class Presenter {
    public void onSaveClick(View view, Task task){}
}
```

```
android:onClick="@{(theView) -> presenter.onSaveClick(theView, task)}"
```

您可以使用带有多个参数的lambda表达式：

```
public class Presenter {
    public void onCompletedChanged(Task task, boolean completed){}
}
```

```
<CheckBox
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:onCheckedChanged="@{(cb, isChecked) -> presenter.completeChanged(task, isChecked)}" />
```

如果正在侦听的事件返回一个类型不是void的值，则表达式也必须返回相同类型的值。例如，如果要监听长按事件，则表达式应返回布尔值。

```
public class Presenter {
    public boolean onLongClick(View view, Task task) { }
}
```

```
android:onLongClick="@{(theView) -> presenter.onLongClick(theView, task)}"
```

如果由于null对象而无法计算表达式，数据绑定将返回该类型的默认值。例如，引用类型为null，int类型为0，boolean类型为false，等等。

如果需要使用带谓词的表达式(例如三元组)，可以使用`void`作为符号。

```
android:onClick="@{(v) -> v.isVisible() ? doSomething() : void}"
```

> **注意**：侦听器表达式非常强大，可以使代码非常容易阅读。另一方面，包含复杂表达式的侦听器会使布局难以阅读和维护。这些表达式应该与将可用数据从UI传递到回调方法一样简单。
您应该在从侦听器表达式调用的回调方法中实现业务逻辑。

### 导入和变量和包含

数据绑定库提供了导入、变量和包含等特性。导入使在布局文件中引用类变得容易。变量允许您描述可以在绑定表达式中使用的属性。包含让你在你的应用程序中重用复杂的布局。

#### 导入

导入允许您轻松地引用布局文件中的类，就像在托管代码中一样。数据元素中可以使用零个或多个导入元素。以下代码示例将View类导入布局文件：

```
<data>
    <import type="android.view.View"/>
</data>
```

导入View该类允许您从绑定表达式中引用它。下面的例子展示了如何引用View类的VISIBLE和GONE常量。

```
<TextView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text="@{user.lastName}"
    android:visibility="@{user.isAdult ? View.VISIBLE : View.GONE}" />
```

当存在类名冲突时，可以将其中一个类重命名为别名。下面的例子将com.example.real.estate包中的View类重命名为Vista:

```
<import type="android.view.View" />
<import type="com.example.real.estate.View" alias="Vista" />
```

在布局文件中您可以使用Vista来引用com.example.real.estate.View，使用View来引用android.view.View。

导入的类型可以用作变量和表达式中的类型引用。下面的例子显示了作为变量类型使用的User和List:

```
<data>
    <import type="com.example.User"/>
    <import type="java.util.List"/>
    <variable name="user" type="User"/>
    <variable name="userList" type="List&lt;User>"/>
</data>
```

还可以使用导入的类型转换表达式的一部分。下面的示例将connection属性转换为User类型:

```
<TextView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text="@{((User)(user.connection)).lastName}" />
```

当引用表达式中的静态字段和方法时，也可以使用导入的类型。下面的代码导入MyStringUtils类并引用它的capitalize方法:

```
<data>
    <import type="com.example.MyStringUtils"/>
    <variable name="user" type="com.example.User"/>
</data>
…
<TextView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text="@{MyStringUtils.capitalize(user.lastName)}" />
```

#### 变量

您可以在数据元素中使用多个变量元素。每个变量元素描述可以在布局上设置的属性，用于布局文件中的绑定表达式。下面的示例声明的user，image和note变量：

```
<data>
    <import type="android.graphics.drawable.Drawable"/>
    <variable name="user" type="com.example.User"/>
    <variable name="image" type="Drawable"/>
    <variable name="note" type="String"/>
</data>
```

变量类型在编译时被检查，所以如果一个变量实现了Observable或者是一个Observable集合，那么它应该反映在类型中。
如果该变量是一个基类或接口，而该基类或接口没有实现可观察的接口，则不观察变量。

当不同配置(例如，横向或纵向)有不同的布局文件时，变量就会组合起来。这些布局文件之间不应该有相互冲突的变量定义。

数据绑定库为布局中声明的每个变量生成setter和getter方法。这些变量接受默认的托管代码值，直到setter被调用，引用类型为null，int为0，布尔值为false，等等。

> **注意**：将生成一个名为context的特殊变量，以便根据需要在绑定表达式中使用。context的值是根视图的getContext()方法中的上下文对象。

#### 包含

通过使用app名称空间和属性中的变量名，可以将变量从包含布局传递到包含布局的绑定中。下面的示例显示了name.xml和contact.xml布局文件中的user变量:

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:bind="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="user"
            type="com.example.User" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <include
            layout="@layout/name"
            bind:user="@{user}" />

        <include
            layout="@layout/contact"
            bind:user="@{user}" />
    </LinearLayout>
</layout>
```

数据绑定不支持include作为merge元素的直接子元素。例如，不支持以下布局：

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:bind="http://schemas.android.com/apk/res-auto">

    <data>
        <variable
            name="user"
            type="com.example.User" />
    </data>

    <merge><!-- Doesn't work -->
        <include
            layout="@layout/name"
            bind:user="@{user}" />

        <include
            layout="@layout/contact"
            bind:user="@{user}" />
    </merge>
</layout>
```


## 使用可观察的数据对象

数据绑定库提供了类和方法来方便地观察数据的变化。当底层数据源发生更改时，您不必担心刷新UI。你可以使你的变量或它们的性质可见。库允许您使对象、字段或集合成为可观察的。

任何普通对象都可以用于数据绑定，但是修改对象不会自动导致UI更新。数据绑定可用于使数据对象能够在其数据发生更改时通知其他对象(称为侦听器)。有三种不同类型的可观察类:对象、字段和集合。

### 可观察对象

实现了Observable接口的类允许注册侦听器，侦听器希望在Observable对象的属性更改时得到通知。

Observable接口有一个添加和删除侦听器的机制，但是必须决定何时发送通知。为了简化开发，数据绑定库提供了BaseObservable类，它实现了侦听器注册机制。
实现BaseObservable的数据类负责在属性发生更改时通知。这是通过为getter分配一个Bindable注释，并在setter中调用notifyPropertyChanged()方法来实现的，如下面的示例所示:

```
private static class User extends BaseObservable {
    private String firstName;
    private String lastName;

    @Bindable
    public String getFirstName() {
        return this.firstName;
    }

    @Bindable
    public String getLastName() {
        return this.lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }
}
```

数据绑定在模块包中生成一个名为BR的类，其中包含用于数据绑定的资源的id。可绑定注释在编译期间在BR类文件中生成一个条目。
如果数据类的基类无法更改，则可以使用PropertyChangeRegistry对象实现可观察接口，以便有效地注册和通知侦听器。

### 可观察的字段

创建实现Observable接口的类涉及到一些工作，如果类只有几个属性，那么这些工作就不值得。在这种情况下，您可以使用通用的Observable类和以下特定于原语的类使字段成为可观察的:

* ObservableBoolean
* ObservableByte
* ObservableChar
* ObservableShort
* ObservableInt
* ObservableLong
* ObservableFloat
* ObservableDouble
* ObservableParcelable

可观察字段是包含一个字段的可观察对象。原始版本在访问操作期间避免装箱和解箱。
要使用这种机制，可以在Java编程语言中创建一个`public final`属性，或者在Kotlin中创建一个只读属性，如下面的示例所示:

```
private static class User {
    public final ObservableField<String> firstName = new ObservableField<>();
    public final ObservableField<String> lastName = new ObservableField<>();
    public final ObservableInt age = new ObservableInt();
}
```

要访问字段值，使用set()和get()访问器方法，如下所示:

```
user.firstName.set("Google");
int age = user.age.get();
```

> **注意**：Android Studio 3.1及更高版本允许您用LiveData对象替换可观察字段，这为您的应用程序提供了额外的好处。

### 可观察的集合

一些应用程序使用动态结构来保存数据。可观察的集合允许使用键访问这些结构。

当键是引用类型(如String)时，ObservableArrayMap类很有用，如下面的例子所示:

```
ObservableArrayMap<String, Object> user = new ObservableArrayMap<>();
user.put("firstName", "Google");
user.put("lastName", "Inc.");
user.put("age", 17);
```

在布局中，可以使用字符串键找到map，如下图所示:

```
<data>
    <import type="android.databinding.ObservableMap"/>
    <variable name="user" type="ObservableMap<String, Object>"/>
</data>
…
<TextView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text="@{user.lastName}" />

<TextView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text="@{String.valueOf(1 + (Integer)user.age)}" />
```

当键为整数时，ObservableArrayList类很有用，如下所示:

```
ObservableArrayList<Object> user = new ObservableArrayList<>();
user.add("Google");
user.add("Inc.");
user.add(17);
```

在布局中，可以通过索引访问列表，如下例所示:

```
<data>
    <import type="android.databinding.ObservableList"/>
    <import type="com.example.my.app.Fields"/>
    <variable name="user" type="ObservableList<Object>"/>
</data>
…
<TextView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text='@{user[Fields.LAST_NAME]}' />

<TextView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text='@{String.valueOf(1 + (Integer)user[Fields.AGE])}' />
```


## 生成的绑定类

数据绑定库生成绑定类，用于访问布局的变量和视图。生成的绑定类将布局变量与布局中的视图链接起来。绑定类的名称和包可以定制。所有生成的绑定类都继承自ViewDataBinding类。

### 创建绑定对象

绑定对象应该在扩展布局之后立即创建，以确保视图层次结构在绑定到布局中包含表达式的视图之前没有被修改。将对象绑定到布局的最常见方法是使用绑定类上的静态方法。
您可以使用binding类的inflation()方法扩展视图层次结构并将对象绑定到它，如下面的示例所示:

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyLayoutBinding binding = MyLayoutBinding.inflate(getLayoutInflater());
}
```

除了LayoutInflater对象之外，还有另一个版本的inflater()方法接受ViewGroup对象，如下面的示例所示:

```
MyLayoutBinding binding = MyLayoutBinding.inflate(getLayoutInflater(), viewGroup, false);
```

如果使用不同的机制对布局进行扩展，则可以单独绑定，如下:

```
MyLayoutBinding binding = MyLayoutBinding.bind(viewRoot);
```
有时绑定类型不能预先知道。在这种情况下，可以使用DataBindingUtil类创建绑定，如下面的代码片段所示:

```
View viewRoot = LayoutInflater.from(this).inflate(layoutId, parent, attachToParent);
ViewDataBinding binding = DataBindingUtil.bind(viewRoot);
```

如果您在Fragment， ListView或RecyclerView适配器中使用数据绑定，您可能更喜欢使用bindings类或DataBindingUtil类的inflate()方法，如下面的代码示例所示:

```
ListItemBinding binding = ListItemBinding.inflate(layoutInflater, viewGroup, false);
// or
ListItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, viewGroup, false);
```

### ViewStubs

与普通视图不同，ViewStub对象从一个不可见的视图开始。当它们被显示或被明确告知要展示时，它们会通过展示另一个布局来替换它们自己。

因为ViewStub基本上从视图层次结构中消失，所以绑定对象中的视图也必须消失以允许通过垃圾回收声明。
因为视图是最终的，所以ViewStubProxy对象代替生成的绑定类中的ViewStub，使您在ViewStub存在时可以访问它，并且还可以在展示时访问ViewStub的视图层次结构。

在展示另一个布局时，必须为新布局建立绑定。因此，ViewStubProxy必须在必要时监听ViewStub OnInflateListener并建立绑定。
由于在给定的时间内只能存在一个侦听器，因此ViewStubProxy允许您设置OnInflateListener，它在建立绑定之后调用OnInflateListener。

### 立即绑定

当一个变量或可观察对象发生变化时，绑定计划在下一帧之前更改。但是，有时必须立即执行绑定。要强制执行，请使用executePendingBindings()方法。

### 动态变量

有时，特定的绑定类是未知的。例如，RecyclerView.Adapter针对任意布局的操作不知道特定的绑定类。它仍然必须在调用onBindViewHolder()方法期间分配绑定值。

在以下示例中，RecyclerView绑定的所有布局都具有item变量。该BindingHolder对象有一个getBinding()返回ViewDataBinding基类的方法 。

```
public void onBindViewHolder(BindingHolder holder, int position) {
    final T item = items.get(position);
    holder.getBinding().setVariable(BR.item, item);
    holder.getBinding().executePendingBindings();
}
```

> 注意:数据绑定库在模块包中生成一个名为BR的类，该类包含用于数据绑定的资源的ID。在上面的示例中，库自动生成BR.item变量。

> 您可以在后台线程中更改数据模型，只要它不是集合即可。数据绑定在计算期间本地化每个变量/字段以避免任何并发问题。

### 自定义绑定类名称

默认情况下，绑定类是基于布局文件的名称生成的，以大写字母开始，删除下划线(_)，将后面的字母大写，并添加Binding后缀。该类放在模块包下的databinding包中。
例如，布局文件contact_item.xml生成ContactItemBinding类。如果模块包是com.example.my.app，则绑定类放在com.example.my.app.databinding包中。
绑定类可以通过调整数据元素的class属性来重命名或放在不同的包中。例如，下面的布局在当前模块的databinding包中生成ContactItem绑定类:

```
<data class="ContactItem">
    …
</data>
```

通过在类名前面加上句号，可以在不同的包中生成绑定类。下面的示例在模块包中生成绑定类:

```
<data class=".ContactItem">
    …
</data>
```

您还可以在希望生成绑定类的地方使用完整的包名。下面的示例在com.example包创建ContactItem绑定类：

```
<data class="com.example.ContactItem">
    …
</data>
```


## 绑定适配器

对于每个布局表达式，都有一个绑定适配器，用于进行设置相应属性或侦听器所需的框架调用。
例如，绑定适配器可以调用setText()方法来设置文本属性，你可以在绑定数据库的android.databinding.adapters包中查看相关代码。

下面是android:text属性的一个例子:

```
@BindingAdapter("android:text")
public static void setText(TextView view, CharSequence text) {
    final CharSequence oldText = view.getText();
    if (text == oldText || (text == null && oldText.length() == 0)) {
        return;
    }
    if (text instanceof Spanned) {
        if (text.equals(oldText)) {
            return; // No change in the spans, so don't set anything.
        }
    } else if (!haveContentsChanged(text, oldText)) {
        return; // No content changes, so don't set anything.
    }
    view.setText(text);
}
```

您还可以创建自定义适配器，如下面的示例所示:

```
@BindingAdapter("app:goneUnless")
public static void goneUnless(View view, Boolean visible) {
    view.visibility = visible ? View.VISIBLE : View.GONE;
}
```

绑定适配器负责进行适当的框架调用来设置值。一个例子是设置一个属性值，比如调用setText()方法。另一个例子是设置一个事件监听器，比如调用setOnClickListener()方法。

数据绑定库允许您指定调用的方法来设置值，提供自己的绑定逻辑，并使用适配器指定返回对象的类型。

### 设置属性值

每当绑定值发生更改时，生成的绑定类必须使用绑定表达式调用视图上的setter方法。您可以允许数据绑定库自动确定方法、显式声明方法或提供自定义逻辑来选择方法。

#### 自动方法选择

对于名为example的属性，库会自动尝试找到接受兼容类型作为参数的方法setExample(arg)。不考虑属性的命名空间，只在搜索方法时使用属性名和类型。

例如，给定android:text="@{user.name}"表达式，库查找一个setText(arg)方法，该方法接受user.getName()返回的类型。
如果user.getName()的返回类型是String，则库将查找接受字符串参数的setText()方法。
如果表达式返回int，则库将搜索接受int参数的setText()方法。表达式必须返回正确的类型，如果需要，可以转换返回值。

即使给定名称中不存在属性，数据绑定也可以工作。然后可以使用数据绑定为任何setter创建属性。例如，支持类DrawerLayout没有任何属性，但是有很多setter。
以下布局分别自动使用setScrimColor(int)和setDrawerListener(DrawerListener)方法作为app:scrimColor和app:drawerListener属性的setter ：

```
<android.support.v4.widget.DrawerLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:scrimColor="@{@color/scrim}"
    app:drawerListener="@{fragment.drawerListener}">
```

#### 指定自定义方法名称

有些属性的setter方法与名称不匹配。在这些情况下，属性可以使用BindingMethods注释与setter关联。
注释与类一起使用，可以包含多个 BindingMethod注释，每个注释方法一个注释。
绑定方法是可以添加到应用程序中的任何类的注释。在下面的示例中，android:tint属性与setImageTintList(ColorStateList)方法关联，而不是与setTint()方法关联:

```
@BindingMethods({
        @BindingMethod(type = android.widget.ImageView.class, attribute = "android:tint", method = "setImageTintList"),
        @BindingMethod(type = android.widget.ImageView.class, attribute = "android:tintMode", method = "setImageTintMode"),
})
```

大多数情况下，您不需要在Android框架类中重命名setter。属性已经使用名称约定实现，用于自动查找匹配方法。

#### 提供自定义逻辑

有些属性需要自定义绑定逻辑。例如，android:paddingLeft属性没有关联的setter。而是提供setPadding(left, top, right, bottom)方法。
带有BindingAdapter注释的静态绑定适配器方法允许您自定义如何调用属性的setter。

Android框架类的属性已经创建了BindingAdapter注释。例如，下面的例子显示了paddingLeft属性的绑定适配器:

```
@BindingAdapter("android:paddingLeft")
public static void setPaddingLeft(View view, int padding) {
    view.setPadding(padding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
}
```

参数类型很重要。第一个参数确定与属性关联的视图的类型。第二个参数确定给定属性的绑定表达式中接受的类型。

您还可以拥有接收多个属性的适配器，如下面的示例所示:

```
@BindingAdapter({"imageUrl", "error"})
public static void loadImage(ImageView view, String url, Drawable error) {
    Picasso.get().load(url).error(error).into(view);
}
```

您可以在布局中使用适配器，如下面的示例所示。注意@drawable/venueError引用应用程序中的资源。在资源周围加上@{}使其成为一个有效的绑定表达式。

```
<ImageView app:imageUrl="@{venue.imageUrl}" app:error="@{@drawable/venueError}" />
```

> **注意**：数据绑定库会忽略自定义命名空间以进行匹配。

如果imageUrl和error都用于ImageView对象，并且imageUrl是字符串，error是可绘制的，则调用适配器。
如果您希望在设置任何属性时调用适配器，可以将适配器的可选requireAll标志设置为false，如下面的示例所示:

```
@BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
public static void setImageUrl(ImageView imageView, String url, Drawable placeHolder) {
    if (url == null) {
        imageView.setImageDrawable(placeholder);
    } else {
        MyImageLoader.loadInto(imageView, url, placeholder);
    }
}
```

requireAll参数定义何时使用绑定适配器：

* 如果为true，则所有元素都必须出现在XML定义中。
* 如果为false，丢失的属性引用类型为null，int类型为0，boolean类型为false，等等。

> **注意**：当存在冲突时，您定义的绑定适配器将覆盖Android框架提供的默认适配器。

绑定适配器方法可以选择在其处理程序中接受旧值。获取新旧值的方法应该首先声明属性的所有旧值，然后是新值，如下例所示:

```
@BindingAdapter("android:paddingLeft")
public static void setPaddingLeft(View view, int oldPadding, int newPadding) {
    if (oldPadding != newPadding) {
        view.setPadding(newPadding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }
}
```

事件处理程序只能与一个抽象方法的接口或抽象类一起使用，如下例所示:

```
@BindingAdapter("android:onLayoutChange")
public static void setOnLayoutChangeListener(View view, View.OnLayoutChangeListener oldValue,
                                             View.OnLayoutChangeListener newValue) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        if (oldValue != null) {
            view.removeOnLayoutChangeListener(oldValue);
        }
        if (newValue != null) {
            view.addOnLayoutChangeListener(newValue);
        }
    }
}
```

在布局中使用此事件处理程序，如下所示：

```
<View android:onLayoutChange="@{() -> handler.layoutChanged()}"/>
```

侦听器有多个方法时，必须将其拆分为多个侦听器。例如，View.OnAttachStateChangeListener有两种方法：onViewAttachedToWindow(View)和onViewDetachedFromWindow(View)。
该库提供了两个接口来区分它们的属性和处理程序：

```
@TargetApi(VERSION_CODES.HONEYCOMB_MR1)
public interface OnViewDetachedFromWindow {
  void onViewDetachedFromWindow(View v);
}

@TargetApi(VERSION_CODES.HONEYCOMB_MR1)
public interface OnViewAttachedToWindow {
  void onViewAttachedToWindow(View v);
}
```

因为更改一个侦听器也会影响另一个侦听器，所以需要一个适用于任一属性或适用于两者的适配器。
您可以在注释中设置requireAll为false指定不是必须为每个属性分配绑定表达式，如以下示例所示：

```
@BindingAdapter({"android:onViewDetachedFromWindow", "android:onViewAttachedToWindow"}, requireAll = false)
public static void setListener(View view, OnViewDetachedFromWindow detach, OnViewAttachedToWindow attach) {
    if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1) {
        OnAttachStateChangeListener newListener;
        if (detach == null && attach == null) {
            newListener = null;
        } else {
            newListener = new OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    if (attach != null) {
                        attach.onViewAttachedToWindow(v);
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (detach != null) {
                        detach.onViewDetachedFromWindow(v);
                    }
                }
            };
        }

        OnAttachStateChangeListener oldListener = ListenerUtil.trackListener(view, newListener,
                R.id.onAttachStateChangeListener);
        if (oldListener != null) {
            view.removeOnAttachStateChangeListener(oldListener);
        }
        if (newListener != null) {
            view.addOnAttachStateChangeListener(newListener);
        }
    }
}
```

上面的示例比正常情况稍微复杂一些，因为View类使用addOnAttachStateChangeListener()和removeOnAttachStateChangeListener()方法，
而不是OnAttachStateChangeListener的setter方法。android.databinding.adapters.ListenerUtil类帮助跟踪以前的侦听器，以便在绑定适配器中删除它们。

通过使用@TargetApi(VERSION_CODES.HONEYCOMB_MR1)注释OnViewDetachedFromWindow和OnViewAttachedToWindow接口，
数据绑定代码生成器知道侦听器只应该在运行于Android 3.1 (API级别12)或更高版本时生成，而addOnAttachStateChangeListener()方法支持相同的版本。

### 对象转换

#### 自动对象转换

当从绑定表达式返回Object时，库选择用于设置属性值的方法。Object被转换为所选方法的参数类型。这种行为在使用ObservableMap类存储数据的应用程序中很方便，如下面的例子所示:

```
<TextView
    android:layout_height="wrap_content"
    android:layout_width="wrap_content"
    android:text='@{userMap["lastName"]}' />
```

> **注意**：您还可以使用object.key表示引用map中的值。例如，@{userMap["lastName"]}在上面的例子中可以替换为 @{userMap.lastName}。

表达式中的userMap对象返回一个值，该值自动转换为setText(CharSequence)方法中找到的参数类型，该方法用于设置android:text属性的值。
如果参数类型不明确，则必须在表达式中转换返回类型。

#### 自定义转化

在某些情况下，特定类型之间需要自定义转换。例如，android:background视图的属性需要一个Drawable，但是指定的颜色值是一个整数。
下面的例子显示了一个属性，它期望一个Drawable，但是提供了一个整数:

```
<View
    android:background="@{isError ? @color/red : @color/white}"
    android:layout_height="wrap_content"
    android:layout_width="wrap_content" />
```

无论何时需要Drawable并返回一个整数，都应该将int转换为ColorDrawable。转换可以使用带有BindingConversion注释的静态方法完成，如下所示:

```
@BindingConversion
public static ColorDrawable convertColorToDrawable(int color) {
    return new ColorDrawable(color);
}
```

但是，绑定表达式中提供的值类型必须是一致的。你不能在同一个表达式中使用不同的类型，如下面的例子所示:

```
<View
    android:background="@{isError ? @drawable/error : @color/white}"
    android:layout_height="wrap_content"
    android:layout_width="wrap_content" />
```


## 将布局视图绑定到体系结构组件

AndroidX库包含体系结构组件，您可以使用这些组件设计健壮、可测试和可维护的应用程序。数据绑定库与体系结构组件无缝协作，从而进一步简化UI的开发。
应用程序中的布局可以绑定到体系结构组件中的数据，这些组件已经帮助您管理UI控制器的生命周期，并通知数据中的更改。

### 使用LiveData通知UI有关数据更改的信息

您可以使用LiveData对象作为数据绑定源来自动通知UI有关数据更改的信息。有关此体系结构组件的更多信息，请参阅[LiveData](live_data.md)。

与实现可观察对象(如可观察字段)不同，LiveData对象知道订阅数据更改的观察者的生命周期。
在Android Studio 3.1或更高版本中，可以用数据绑定代码中的LiveData对象替换可观察字段。

要将LiveData对象与绑定类一起使用，需要指定生命周期所有者以定义LiveData对象的范围。以下示例在实例化绑定类之后将活动指定为生命周期所有者：

```
public class ViewModelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate view and obtain an instance of the binding class.
        UserBinding binding = DataBindingUtil.setContentView(this, R.layout.user);

        // Specify the current activity as the lifecycle owner.
        binding.setLifecycleOwner(this);
    }
}
```

您可以使用ViewModel 组件（如[使用ViewModel管理与UI相关的数据](#使用ViewModel管理与UI相关的数据)）将数据绑定到布局。
在ViewModel组件中，您可以使用该LiveData对象转换数据或合并多个数据源。下面的例子展示了如何在ViewModel中转换数据:

```
class ScheduleViewModel extends ViewModel {
    LiveData username;

    public ScheduleViewModel() {
        String result = Repository.userName;
        userName = Transformations.map(result, result -> result.value);
    }
}
```

### 使用ViewModel管理与UI相关的数据

数据绑定库与ViewModel组件无缝协作，组件公开布局观察到的数据并对其更改做出反应。通过使用ViewModel数据绑定库中的组件，您可以将UI逻辑从布局移动到组件中，这些组件更易于测试。
数据绑定库可确保在需要时绑定和取消绑定数据源。剩下的大部分工作都在于确保您公开正确的数据。有关此体系结构组件的更多信息，请参阅[ViewModel](view_model.md)。

要将ViewModel组件与数据绑定库一起使用，必须实例化从ViewModel类继承的组件，获取绑定类的实例，并将ViewModel组件分配给绑定类中的属性。以下示例显示如何将该组件与库一起使用：

```
class ViewModelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Obtain the ViewModel component.
        UserModel userModel = ViewModelProviders.of(getActivity()).get(UserModel.class);

        // Inflate view and obtain an instance of the binding class.
        UserBinding binding = DataBindingUtil.setContentView(this, R.layout.user);

        // Assign the component to a property in the binding class.
        binding.userModel = userModel;
    }
}
```

在布局中，ViewModel使用绑定表达式将组件的属性和方法分配给相应的视图，如以下示例所示：

```
<CheckBox
    android:id="@+id/rememberMeCheckBox"
    android:checked="@{userModel.rememberMe}"
    android:onCheckedChanged="@{() -> userModel.rememberMeChanged()}" />
```

### 使用ObservableViewModel可以更好地控制绑定适配器

您可以使用实现了Observable的ViewModel组件通知其他应用程序组件数据中的更改，类似于您使用LiveData对象的方式。

在某些情况下，即使失去了LiveData的生命周期管理功能，您也可能宁愿使用实现Observable接口的ViewModel组件，而不是使用LiveData对象。
使用ViewModel实现的组件Observable可以更好地控制应用程序中的绑定适配器。例如，此模式使您可以在数据更改时更好地控制通知，还允许您指定自定义方法以在双向数据绑定中设置属性的值。

要实现一个可观察的ViewModel组件，您必须创建一个继承自ViewModel类并实现Observable接口的类。
当观察者使用addOnPropertyChangedCallback() 和 removeOnPropertyChangedCallback() 方法订阅或取消订阅通知时，您可以提供自定义逻辑。
您还可以提供当notifyPropertyChanged()方法中属性更改时运行的自定义逻辑。以下代码示例演示如何实现Observable ViewModel：

```
/**
 * A ViewModel that is also an Observable,
 * to be used with the Data Binding Library.
 */
class ObservableViewModel extends ViewModel implements Observable {
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Override
    protected void addOnPropertyChangedCallback(
            Observable.OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    protected void removeOnPropertyChangedCallback(
            Observable.OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}
```


## 双向数据绑定

数据绑定库支持双向数据绑定。用于此类绑定的表示支持接收对属性的数据更改并同时侦听用户对该属性的更新。

使用单向数据绑定，您可以在属性上设置值，并设置对该属性中的更改作出反应的侦听器：

```
<CheckBox
    android:checked="@{viewModel.rememberMe}"
    android:id="@+id/rememberMeCheckBox"
    android:onCheckedChanged="@{viewModel.rememberMeChanged}" />
```

双向数据绑定提供了此过程的快捷方式：

```
<CheckBox
    android:checked="@={viewModel.rememberMe}"
    android:id="@+id/rememberMeCheckBox" />
```

`@={}`符号(重要的是包含"="符号)接收对属性的数据更改，同时侦听用户更新。

为了对备份数据的变化做出反应，可以将布局变量设置为Observable的实现，通常是BaseObservable，并使用@Bindable注释，如下面的代码片段所示:

```
public class LoginViewModel extends BaseObservable {
    // private Model data = ...

    @Bindable
    public Boolean getRememberMe() {
        return data.rememberMe;
    }

    public void setRememberMe(Boolean value) {
        // Avoids infinite loops.
        if (data.rememberMe != value) {
            data.rememberMe = value;

            // React to the change.
            saveData();

            // Notify observers of a new value.
            notifyPropertyChanged(BR.remember_me);
        }
    }
}
```

因为Bindable属性的getter方法被称为getRememberMe()，所以属性对应的setter方法自动使用setRememberMe()这个名称。

### 使用自定义属性进行双向数据绑定

该平台为最常见的双向属性和更改侦听器提供双向数据绑定实现，您可以将其用作应用程序的一部分。
如果要对自定义属性使用双向数据绑定，则需要使用 @InverseBindingAdapter 和 @InverseBindingMethod 注释。

例如，如果您想在名为MyView的自定义视图中启用对"time"属性的双向数据绑定，请完成以下步骤:

1. 注释设置初始值的方法，并在值更改时使用@BindingAdapter进行更新:

    ```
    @BindingAdapter("time")
    public static void setTime(MyView view, Time newValue) {
        // Important to break potential infinite loops.
        if (view.time != newValue) {
            view.time = newValue;
        }
    }
    ```

2. 使用@InverseBindingAdapter方法注释从视图中读取值:

    ```
    @InverseBindingAdapter("time")
    public static Time getTime(MyView view) {
        return view.getTime();
    }
    ```

    此时，数据绑定知道当数据发生更改时应该做什么(它调用@BindingAdapter注释的方法)，以及当视图属性发生更改时应该调用什么(它调用InverseBindingListener)。
    但是，它不知道属性何时或如何更改。

    为此，您需要在视图上设置一个侦听器。它可以是与自定义视图关联的自定义侦听器，也可以是通用事件，例如焦点丢失或文本更改。
    将@BindingAdapter注释添加到方法中，该方法为属性的更改设置侦听器:

    ```
    @BindingAdapter("app:timeAttrChanged")
    public static void setListeners(MyView view, final InverseBindingListener attrChange) {
        // Set a listener for click, focus, touch, etc.
    }
    ```

    侦听器包含一个InverseBindingListener作为参数。使用InverseBindingListener告诉数据绑定系统属性已经更改。然后，系统可以开始调用使用@InverseBindingAdapter等注释的方法。

    > **注意**：每个双向绑定都会生成一个合成事件属性。此属性与基本属性具有相同的名称，但它包含后缀 "AttrChanged"。
    合成事件属性允许库创建一个使用@BindingAdapter注释的方法，以将事件侦听器关联到适当的视图实例。

    实际上，这个侦听器包含一些重要的逻辑，包括用于单向数据绑定的侦听器。例如，要查看文本属性更改的适配器TextViewBindingAdapter。

### 转换器

如果绑定到View对象的变量需要在显示之前以某种方式进行格式化，翻译或更改，则可以使用Converter对象。

例如，获取EditText显示日期的对象：

```
<EditText
    android:id="@+id/birth_date"
    android:text="@={Converter.dateToString(viewModel.birthDate)}" />
```

该viewModel.birthDate属性包含一个Long类型的值，因此需要使用转换器对其进行格式化。

因为使用了双向表达式，所以还需要一个反向转换器，让库知道如何将用户提供的字符串转换回支持数据类型(在本例中为Long)。
这个过程是通过向其中一个转换器添加@InverseMethod注释来完成的，并让这个注释引用逆转换器。以下代码段中显示了此配置的示例：

```
public class Converter {

    @InverseMethod("stringToDate")
    public static String dateToString(EditText view, long oldValue, long value) {
        // Converts long to String.
    }

    public static long stringToDate(EditText view, String oldValue, String value) {
        // Converts String to long.
    }
}
```

### 使用双向数据绑定的无限循环

在使用双向数据绑定时，注意不要引入无限循环。当用户更改属性时，将调用使用@InverseBindingAdapter注释的方法，并将该值分配给支持属性。
然后，这将调用使用@BindingAdapter注释的方法，这将触发对使用@InverseBindingAdapter注释的方法的另一个调用，以此类推。
因此，通过比较使用@BindingAdapter注释的方法中的新值和旧值，打破可能的无限循环是很重要的。

当您使用下表中的属性时，平台提供了对双向数据绑定的内置支持。
[Two-way attributes](https://developer.android.google.cn/topic/libraries/data-binding/two-way#two-way-attributes)。