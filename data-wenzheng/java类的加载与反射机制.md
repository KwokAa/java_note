## java类的加载与反射机制

### 类的加载来源

使用不同的类加载器，可以从不同的来源加载类的二进制数据。

1. 从本地文件系统加载class文件。
2. 从JAR包加载class文件。JVM可以从JAR文件中直接加载该class文件。
3. 从网络加载class文件。
4. 把一个Java源文件动态编译，并执行加载。

### 类初始化的时机

当java程序首次通过下面6种方式来使用某个类或接口时，系统就会初始化该类或者接口。

1. 创建类的实例。为某个类创建实例的方式包括：使用new操作符来创建实例、通过反射机制来创建实例、通过反序列化的方式来创建实例。
2. 调用某个类的类方法。
3. 访问某个类或接口的类变量、为该类变量赋值。
4. 使用反射方式来强制创建某个类或接口对应的java.lang.Class对象。例如代码：Class.forName("Person")，如果系统还未初始化Person类，则这行代码将会导致该Person类被初始化，并返回Person类对应的java.lang.Class对象。
5. 初始化某个类的子类。当初始化某个类的子类时，该子类的所有父类都会被初始化。
6. 直接使用java.exe命令来运行某个主类。当运行某个主类时，程序会先初始化该主类。

需要注意的是：对于一个final型的类变量，如果该类变量的值在编译时就可以确定下来，那么这个类变量就相当于“宏变量”。Java编译器会在编译时直接把这个类变量出现的地方替换成它的值，因此，即使程序使用该静态类变量，也不会导致该类的初始化。

```java
class A{
	static {
		System.out.println("静态初始化块A");
	}
}
class MyTest extends A
{
	static
	{
		System.out.println("静态初始化块MyTest");
	}
	static final String COMPILECONSTANT_STRING = 3 + "01";
	//static final String COMPILECONSTANT_STRING = System.currentTimeMillis() + "01";
	//这种情况下，必须等到程序实际运行时才能知道COMPILECONSTANT_STRING的值，所以必须保存对MyTest的引用。这样就会导致MyTest类会被初始化
}
public class ReflectMechanism {
	public static void main(String[] args) {
		System.out.println(MyTest.COMPILECONSTANT_STRING);
	}
}
```

当使用ClassLoader类的loadClass方法来记载某个类时，该方法只是加载该类，并未进行初始化。使用Class的forName静态方法才会导致强制初始化该类。

```java
class MyTest
{
	static
	{
		System.out.println("静态初始化块MyTest");
	}
	static final String COMPILECONSTANT_STRING = System.currentTimeMillis() + "01";
}
public class ReflectMechanism {
	public static void main(String[] args) throws ClassNotFoundException {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		cl.loadClass("MyTest");
		Class.forName("MyTest");
	}
}
```

### 类加载器

类加载器负责将.class文件加载到内存中，并为之创建java.lang.Class对象。一旦一个类被载入JVM中，同一个类就不会被再次载入了。一个载入JVM的类有一个唯一的标识。在Java中，一个类用其全限定类名作为标识。但在JVM中，一个类用其全限定类名和其类加载器作为唯一标识。例如，如果在pg的包中有一个名为Person的类，被类加载器ClassLoader的实例k1负责加载，则该Person类对应的Class对象在JVM中标识为(Person、pg、k1)。这意味着两个类加载器加载的同名类：(Person、pg、k1)和(Person、pg、k2)是不同的、它们所加载的类也是完全不同、互不兼容的。

当JVM启动时，会形成由三个类加载器组成的初始类加载器层次结构：

- Bootstrap ClassLoader：根类加载器
- Extension ClassLoader：扩展类加载器
- System ClassLoader：系统类加载器

Bootstrap ClassLoader被称为引导类加载器，负责加载Java的核心类。平时编程所使用的String、System这些核心类库就是在这个时候加载的。这些常用的类位于C:\Program Files\Java\jre1.8.0_191\lib中。根类加载器并不是java.lang.ClassLoader的子类，而是由JVM自身实现的。

Extension ClassLoader被称为扩展类加载器，它负责加载JRE的扩展目录（C:\Program Files\Java\jre1.8.0_191\lib\ext或者有java.ext.dirs系统属性指定的目录）中JAR包的类。通过这种方式可以为Java扩展核心类以外的新功能，只需将自己开发的类打包成JAR文件，然后放入JAVA_HOME/jre/lib/ext路径即可。

System ClassLoader被称为系统类加载器，它负责在JVM启动时加载来自java命令的-classpath选项、java.class.path系统属性，或CLASSPATH环境变量所指定的JAR包和类路径。用户程序中可以通过ClassLoader的静态方法getSystemClassLoader()来获取系统类加载器。如果没有特别指定，则用户自定义的类加载器都以类加载器作为父加载器。

#### 类加载机制

JVM的类加载机制主要有三种：

1. 全盘负责。当一个类加载器负责加载某个Class时，该Class所依赖的和引用的其他Class也将由该类加载器负责载入，除非显式使用另外一个加载器来载入。
2. 父类委托。先让父类加载试图加载该Class。父类无法加载时才尝试从自己的类路径中加载该类。注意：这里的父类加载器于继承关系上的父子关系不同。Bootstrap ClassLoader <= Extension ClassLoader <= System ClassLoader <= 用户类加载器，这是类加载器之间的父子关系。
3. 缓存机制。缓存机制将会保证所有加载过的Class都会被缓存，当程序需要使用某个class时，类加载器先从缓存区中搜索该Class，只有当缓存区中不存在该Class对象时，系统才会读取该类对应的二进制数据，并将其转换成Class对象，存入缓存区中。

```java
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class ReflectMechanism {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
		System.out.println("系统列加载器" + systemLoader);
		/**
		 * 获取系统加载器的加载路径--通常由CLASSPATH环境变量指定
		 * 如果操作系统没有指定CLASSPATH环境变量，则默认以当前路径作为系统类加载器的加载
		 * 路径
		 */
		Enumeration<URL> eml = systemLoader.getResources("");
		while (eml.hasMoreElements()) {
			URL url = (URL) eml.nextElement();
			System.out.println(url);
		}
		
		ClassLoader extensionLoader = systemLoader.getParent();
		System.out.println("扩展类加载器的加载路径" + System.getProperty("java.ext.dirs"));
		System.out.println("扩展类加载器的parent: " + extensionLoader.getParent());
	}
}

//程序输出时
/*系统列加载器sun.misc.Launcher$AppClassLoader@73d16e93
file:/E:/eclipse/workspace/JavaLearn/bin/
扩展类加载器的加载路径C:\Program Files\Java\jre1.8.0_191\lib\ext;C:\WINDOWS\Sun\Java\lib\ext
扩展类加载器的parentnull*/
```

因为根类加载器并没有继承ClassLoader抽象类，所以扩展加载器的getParent()方法返回null。但实际上，扩展类加载器的父类加载器是根类加载器。

类加载器加载Class大致经过的8个步骤：

1. 检测此Class是否载入过（即在缓存区中是否有此Class），如果有直接跳到第8步，否则执行第2步。
2. 如果父类加载器不存在（如果没有父类加载器，则要么parent一定是根类加载器，要么本身就是根类加载器），则跳到第4步执行；如果父类加载器存在，则直接执行第3步。
3. 请求使用父类加载器去载入目标类，如果成功载入则跳到第8步，否则执行第5步
4. 请求使用根类加载器来载入目标类，如果成功载入则跳到第8步，否则跳到第7步。
5. 当前类加载器尝试寻找Class文件（从与此ClassLoader相关的类路径中寻找），如果找到则执行第6步，如果找不到则跳到第7步
6. 从文件中载入Class，成功载入后跳到第8步。
7. 抛出ClassNotFoundException异常。
8. 返回对应的java.lang.Class对象。

第5、6步允许重写ClassLoader的findClass()方法来实现自己的载入策略，甚至重写loadClass()方法来实现自己的载入过程。

#### 创建并使用自定义的类加载器

JVM中除了根类加载器之外的所有类加载器都是ClassLoader子类的实例，程序员可以通过扩展ClassLoader来自定义类加载器。

ClassLoader类有两个关键的方法：

- loadClass(String name, boolean resolve): 该方法为ClassLoader的入口点，根据指定名称来加载类，系统就是通过调用ClassLoader的该方法来获取指定类对应的Class对象。
- findClass(String name): 根据指定名称来查找类。

loadClass方法的执行步骤如下：

1. 用findLoadedClass(String)来检查是否加载类，如果已经加载就直接返回。
2. 在父类加载器上调用loadClass()方法，如果父类加载器上不能够加载该类，则调用自己的findClass方法来加载。如果父类加载器为null，则使用根类加载器来加载。
3. 调用findClass(String)方法查找类。

可以看出，重写findClass()方法可以避免覆盖默认类加载器的父类委托、缓冲机制两种策略：如果重写loadClass()方法，则实现逻辑更为复杂。

在ClassLoader里还有一个核心方法：Class defineClass(String name, byte[] b, int off, int len)，该方法负责将指定类的字节码文件（即Class文件，如 Hello.class）读入字节数组byte[]b内，并把它转换为Class对象。

ClassLoader的其他有用的方法还有：

1. findSystemClass(String name)：从本地文件系统装入文件。它在本地文件系统中寻找类文件，如果存在，就使用defineClass()方法将原始字节转换为Class对象，以将该文件转换为类。
2. static getSystemClassLoader(): 这是一个静态方法，用于返回系统类加载器。
3. getParent()：获取该类加载器的父类加载器。

```java
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CompileClassLoader extends ClassLoader{
	
	//读取文件中的内容
	private byte[] getBytes(String filename) throws IOException {
		File file = new File(filename);
		long len = file.length();
		byte[] row = new byte[(int)len];
		try(FileInputStream fin = new FileInputStream(file)) 
		{
			//一次行读取整个Class文件的全部二进制数据
			int r = fin.read(row);
			if (r != len) {
				throw new IOException("无法读取全部文件：" + r + " != " + len);
			}
			return row;
		}
	}
	
	//定义编译指定Java文件的方法
	private boolean compile(String javaFile) throws IOException {
		System.out.println("CompileClassLoader：正在编译 " + javaFile + "....");
		//调用系统的javac命令
		Process p = Runtime.getRuntime().exec("javac " + javaFile);
		try {
			p.waitFor();
		} catch (Exception e) {
			System.out.println(e);
		}
		//获取javac线程的退出值
		int ret = p.exitValue();
		return ret == 0;
	}
	
	/**
	 * 这里是最关键的
	 * 重写ClassLoader的findClass方法
	 * @throws ClassNotFoundException 
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class clazz = null;
		//将包路径中的.替换成/
		String fileStub = name.replace(".", "/");
		String javaFileName = fileStub + ".java";
		String classFilename = fileStub + ".class";
		File javaFile = new File(javaFileName);
		File classFile = new File(classFilename);
		
		//当指定Java源文件存在且Class文件不存在，或者
		//Java源文件的修改时间比Class文件的修改时间晚时，重新编译
		if ((javaFile.exists() && !(classFile.exists())) 
				|| javaFile.lastModified() > classFile.lastModified()) {
			try {
				//如果编译失败或者Class文件不存在
				if (!compile(javaFileName) || !classFile.exists()) {
					throw new ClassNotFoundException(
							"ClassNotFoundException: " + javaFileName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//如果Class文件已经存在，系统负责将该文件转换成Class对象
		if (classFile.exists()) {
			try {
				//将Class文件的二进制数据读入数组
				byte[] raw = getBytes(classFilename);
				//调用ClassLoader的defineClass方法将二进制数据转换成Class对象
				clazz = defineClass(name, raw, 0, raw.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//如果clazz为null，表明加载失败，则抛出异常
			if (clazz == null) {
				throw new ClassNotFoundException(name);
			}
		}
		return clazz;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//如果运行程序时没有参数，即没有目标类
		args = new String[2];
		args[0] = "Hello";
		args[1] = "疯狂java讲义";
		if (args.length < 1) {
			System.out.println("缺少目标类，请按如下格式运行Java源文件：");
			System.out.println("java CompileClassLoader ClassName");
		}
		//第一个参数是需要运行的类
		String progClass = args[0];
		//剩下的参数将作为运行目标类时的参数
		//将这些参数复制到一个新数组中
		String[] progArgs = new String[args.length - 1];
		System.arraycopy(args, 1, progArgs, 0, progArgs.length);
		CompileClassLoader ccl = new CompileClassLoader();
		//加载需要运行的类
		Class<?> clazz = ccl.loadClass(progClass);
		//获取需要运行的类的主方法
		Method main = clazz.getMethod("main", (new String[0]).getClass());
		Object argsArray[] = {progArgs};
		main.invoke(null, argsArray);
	}
}

```

```java

public class Hello {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println("运行Hello的参数：" + arg);
		}
	}
}

```

#### URLClassLoader类

Java为ClassLoader提供了一个URLClassLoader实现类，该类是系统类加载器和扩展类加载器的父类。URLClassLoader类提供了如下两个构造器：

- URLClassLoader(URL[] urls): 使用默认的父类加载器创建一个ClassLoader对象，该对象将从urls所指定的系统路径来查找并加载类。
- URLClassLoader(URL[] urls, ClassLoader parent): 使用指定的父类加载器创建一个ClassLoader

一旦得到URLClassLoader对象之后，就可以调用该对象的loadClass()方法来加载指定类。

```java
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class URLClassLoaderTest {
	private static Connection conn;
	private static Connection getConn(String url,
			String user, String pass) throws InstantiationException, IllegalAccessException, ClassNotFoundException, MalformedURLException, SQLException {
		if (conn == null) {
			URL[] urls = {new URL("file:mysql-connector-java-5.1.30-bin.jar")};
			//使用默认的ClassLoader作为父ClassLoader
			URLClassLoader myClassLoader = new URLClassLoader(urls);
			//加载MySQL的JDBC驱动，并创建默认实例
			Driver driver = (Driver) myClassLoader.loadClass("com.mysql.jdbc.Driver").newInstance();
			//创建一个设置JDBC连接属性的Properties对象
			Properties props = new Properties();
			props.setProperty("user", user);
			props.setProperty("password", pass);
			//调用Driver对象的connect方法来取得数据库连接
			conn = driver.connect(url, props);
		}
		return conn;
	}
}

```

### 反射机制

#### 获取Class对象

每个类被加载之后，系统就会为该类生成一个Class对象。通过该Class对象就能够访问到JVM中的这个类。获得Class对象的三种方式：

- 使用Class类的forName(String clazzName)静态方法。参数是某个类的全限定类名。
- 调用某个类的class属性来获取该类对应的Class对象。例如，Person.class将会返回Person类的Class对象。
- 调用某个对象的getClass()方法。

第一种和第二种方法对比而言，第二种方法更有优势。因为，代码更安全，程序在编译阶段就可以检查需要访问的Class对象是否存在。程序性能更好，这种方法无需调用方法，性能更好。

#### 使用反射生成并操作对象

##### 创建对象

- 使用Class对象的newInstance()方法来创建该Class对象对应类的实例。这种方法要求该Class对象的对应类有默认构造器，而执行newInstance()方法时，实际上时利用默认构造器来创建该类的实例。
- 先使用Class对象获取指定的Constructor对象，在调用Constructor对象的newInstance()方法来创建该Class对象对应类的实例。

```java
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ObjectPoolFactory {
	//定义一个对象池，前面是对象名，后面是实际对象
	private Map<String, Object> objectPool = new HashMap<String, Object>();
	private Object createObject(String clazzName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Class.forName(clazzName);
		return clazz.newInstance();
	}
	
	public void initPool(String fileName) {
		try (FileInputStream fis = new FileInputStream(fileName)){
			Properties props = new Properties();
			props.load(fis);
			for (String name : props.stringPropertyNames()) {
				//每取出一对key-value对，就根据value创建一个对象
				objectPool.put(name, createObject(props.getProperty(name)));
			}
		} catch (Exception e) {
			System.out.println("读取" + fileName + "异常");
		}
	}
	
	public Object getObject(String name) {
		return objectPool.get(name);
	}
	
	public static void main(String[] args) {
		ObjectPoolFactory pf = new ObjectPoolFactory();
		pf.initPool("E://properties.txt");
		System.out.println(pf.getObject("a"));
		System.out.println(pf.getObject("b"));
	}
}

```

properties文件中的内容：

```java
a=java.util.Date
b=javax.swing.JFrame
```

##### 调用方法

当获得某个类对应的Class对象后，就可以通过该Class对象的getMethods()方法或者getMethod()方法来获取全部方法或者指定方法。可以通过调用每个Method对象的invoke()方法，来执行方法。

Object invoke(Object obj, Object args)：该方法中的obj是执行该方法的主调，后面的args是执行该方法时传入该方法的实参。

当通过Method的invoke()方法来调用对应的方法时，Java会要求程序必须有调用该方法的权限。如果程序确实需要调用某个对象的private方法，则可以先调用Method对象的如下方法：

setAccessible(boolean flag): 将Method对象的accessible设置为指定的布尔值。值为true，指示该Method在使用时应该取消Java语言的访问控制权限检查；值为false，则指示该Method在使用时要实施Java语言的访问权限检查。

setAccessible()方法并不属于Method，而是属于它的父类AccessibleObject。因此Method、Constructor、Field都可以调用该方法。

##### 访问成员变量值

通过Class对象的getFields()或getField()方法可以获取该类所包括的全部成员变量或指定成员变量。Field提供了如下两组方法来读取或设置成员变量值。

- getXxx(Object obj)：获取obj对象的该成员变量的值。此处的Xxx对应8种基本类型，如果该成员变量的类型是引用类型，则取消get后面的Xxx。
- setXxx(Object obj，Xxx val)：设置obj对象的该成员变量的值。此处的Xxx对应8种基本类型，如果该成员变量的类型是引用类型，则取消set后面的Xxx。

使用这两个方法可以随意访问指定对象的所有成员变量，包括private修饰的成员变量。

```java
import java.lang.reflect.Field;

class Person
{
	private String name;
	private int age;
	public String toString() {
		return "Person[name:" + name + ", age:" + age + " ]";
	}
}
public class FieldTest {
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Person person = new Person();
		Class<Person> personClazz = Person.class;
		//使用getDeclaredField方法可以获得各种控制权限的成员变量
		Field nameField = personClazz.getDeclaredField("name");
		nameField.setAccessible(true);
		nameField.set(person, "Leo");
		
		Field ageField = personClazz.getDeclaredField("age");
		ageField.setAccessible(true);
		ageField.setInt(person, 30);
		
		System.out.println(person);
	}
}

```

##### 操作数组

java.lang.reflect包下还提供了一个Array类，Array类代表所有的数组。Array类有如下方法：

- static Object newInstance(Class<?> componetType, int ... length): 创建一个具有指定的元素类型、指定纬度的新数组。
- static xxx getXxx(Object array, int index)：返回array数组中的第index个元素。其中xxx是各种基本数据类型，如果数组元素是引用类型，则该方法变成get(Object array, int index)。
- static void setXxx(Object array, int index, xxx val): 将array数组中第index个元素的值设为val。其中xxx是各种基本数据类型，如果数组元素是引用类型，则该方法变成set(Object array, int index, Object val)。

```java
import java.lang.reflect.Array;

public class ArrayTest1 {
	public static void main(String[] args) {
		Object arr = Array.newInstance(String.class, 10);
		Array.set(arr, 5, "疯狂java讲义");
		Array.set(arr, 6, "疯狂java讲义1");
		
		Object book1 = Array.get(arr, 5);
		Object book2 = Array.get(arr, 6);
		
		System.out.println(book1);
		System.out.println(book2);
	}
}

```

### 代理机制

#### 静态代理

静态代理要求代理类和被代理类都需要实现相同的某个接口。

```java

public interface PersonInterface {
	public void sayHello(String name);
}

```



```java

public class Student implements PersonInterface{

	@Override
	public void sayHello(String name) {
		System.out.println("student say hello : " + name);
	}

}

```



```java
/**
 * 静态代理，对于代理类或者被代理类都需要实现相同的Person接口
 * @author qlxazm
 *
 */
public class ProxyStatic implements PersonInterface{

	private PersonInterface o = null;
	public ProxyStatic(PersonInterface o) {
		this.o = o;
	}
	@Override
	public void sayHello(String name) {
		System.out.println("Proxy name: " + name);
		o.sayHello(name);
	}
	
	public static void main(String[] args) {
		
		//创建被代理的类
		Student student = new Student();
		//根据被代理对象创建一个代理对象
		ProxyStatic proxy = new ProxyStatic(student);
		
		proxy.sayHello("mary");
	}
}

```

#### 动态代理

其中PersonInterface、Student的定义不变。java的动态代理技术只支持接口。

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyInvokationHandler implements InvocationHandler{

	private PersonInterface p = null;
	public MyInvokationHandler(PersonInterface p) {
		this.p = p;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("--正在执行的方法：" + method.getName());
		
		if (args != null) {
			System.out.println("调用方法时使用的参数是：");
			System.out.println("proxy：" + proxy.getClass().getName());
			for (Object arg : args) {
				System.out.println(arg + "--");
			}
			method.invoke(p, args);
		}else {
			System.out.println("调用方法时未传入参数");
		}
		return null;
	}

}

```



```java
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

public class ProxyTest {
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//创建被代理对象
		Student student = new Student();
		//创建InvocationHandler对象
		MyInvokationHandler handler = new MyInvokationHandler(student);
		
		ClassLoader loader = PersonInterface.class.getClassLoader();
		Class<?>[] interfaces = Student.class.getInterfaces();
		
		//方法1：先创建动态代理类，再通过动态代理类创建动态代理对象
		//1、创建动态代理类
		Class proxyClass = Proxy.getProxyClass(loader, interfaces);
		//2、获取带一个InvocationHandler参数的构造方法
		Constructor ctor = proxyClass.getConstructor(
				new Class[] {InvocationHandler.class});
		//3、调用构造函数的newInstance方法来创建动态实例
		PersonInterface newInstance = (PersonInterface)ctor.newInstance(new Object[] {handler});
		//4、使用代理对象
		newInstance.sayHello("ok");
		
		//方法2：直接创建代理对象
		/*PersonInterface proxyInstance = (PersonInterface)Proxy.newProxyInstance(loader, interfaces, handler);
		
		proxyInstance.sayHello("ok");*/
	}
}

```

