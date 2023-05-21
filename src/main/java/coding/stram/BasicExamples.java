package coding.stram;

import lombok.var;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Auther: FU
 * @Date: 2023-05-21 10:52
 * @Description: Stream 的数据源可以无限大
 * 获取流的操作：
 * Collecation.stream（）
 * Stream.of(1,2,3,4,5)
 * 对于IO,我们可以用lines()方法
 * Random.ints() 无限大的数据源中产生
 * 另外JDK还提供了基本数据类型的流：IntStream、LongStream、DoubleStream
 * 流的操作：
 * 中间操作：一个流里面可以有零个和多个中间操作，主要是给流做某种程度的数据映射或者过滤，返回新的流，它是惰性化的，
 *  map/flatMap  映射，将流中每一个元素映射为另外的元素，接收的是一个Funcation,
 *  flatMap:扁平化映射，具体的操作是将多个stream连接成一个stream,这个操作是针对多维数组的，比如容器里面包含容器 ，相当于降维
 *  filter: 过滤，通过测试的元素会被留下来并生成一个新的sream
 *  distinct:去重操作
 *  sortrd：排序操作,如果没有传递参数，那么流中的元素就需要实现Comparable<T> 接口，也可以在使用sorted方法的时候传入一个Comparator<T>
 *    ps:Comparator在java 8 之后被打上了@FuncationalInterface,其他方法都提供了default实现，因此我们可以在sort中使用lambda表达式
 *  peek:有便历的意思，和forEach一样，但是它是一个中间操作，peek接受一个消费型的函数式接口
 *  limit:裁剪操作，接受一个long类型的参数，通过limit之后的元素只会剩下min(n,size)n代表传入的参数，size代表元素个数
 *  skip:表示跳过多少个元素，不过limit是保留前面的元素，skip是保留后面的元素
 * 结束操作：一个流只能有一个，当操作结束后流也结束，无法再被下一环节操作，Terminal操作的执行才会真正开始流的遍历
 *  foreach：终结操作的遍历，和peek一样，但是forEach之后不再返回流
 *  toArray: 和list::toArray用法一致，包含一个重载，默认的toArray()返回一个object[],也可以传入一个IntFuncation<A[]> generator指定数据类型，一般建议第二种
 *  max/min：找出最大或最小的元素，max/min必须传入一个Comparator
 *  count:返回流中的元素数量
 *  reduce：归纳操作，主要是将流中的各个元素结合起来，它需要提供一个起始值，然后按照一定关于规则进行运算，它接收一个二元操作BinaryOperator 函数式接口，从某种意义上来说，
 *  sum、max、average都是特殊的reduce,reduce两个参数和一个参数的区别在于有没有提供一个起始值，如果提供了起始值，则可以返回一个确定的值，如果没有提供起始值，则返回Operational,防止流中没有提供足够的元素
 *  anyMatch\allMatch\noneMatch：测试是否有任意元素、所有元素、没有元素匹配表达式，他们都接收一个推断型函数式接口Predicate
 *  findFirst、findAny、在并行的时候获取元素
 *  collect:包含两个重载，，一个参数和三个参数，
 *  一个参数的collect，我们都直接传入Collectors中的方法引用即可：
 *  三个参数：
 *      Supplier:用于产生最后存放元素的容器的生产者
 *      accumulator:将元素添加到容器中的方法
 *      combiner：将分段元素全部添加到容器中的方法
 * Collectors：处理包含很多转换器之外，还包括groupBy(),它和sql中的groupBy一样都是分组，返回一个Map
 *    groupingBy可以接受3个参数，分别是
 *          第一个参数：分组按照什么分类
 *          第二个参数：分组最后用什么容器保存返回（当只有两个参数是，此参数默认为HashMap）
 *          第三个参数：按照第一个参数分类后，对应的分类的结果如何收集
 *   ps IntStream、LongStream、doubleStream是没有collect()方法的，因为对于基本数据类型，要进行装箱、拆箱操作，SDK并没有将它放入流中，对于基本数据类型流，我们只能将其toArray()
 */
public class BasicExamples {
    @Test
    public void test_mapfilter(){
        Stream.of(1, 2, 3, 4, 5, 6)
                //.map(x -> x.toString())
                .map(x->x+x)//lambda表达式 实现这个接口中抽象方法的形参列表 -> 抽象方法的处理
                /**
                 * lambda表达式不是万能的，它需要函数式接口的支持；
                 * 函数式接口：只包含一个抽象方法的接口，称为函数式接口；所以lambda表达式就越是对函数式接口的一种简写方法，
                 *      1、为什么只能有一个抽象方法呢？因为如果有多个抽象方法，这个接口就不是函数式接口,因为简写的时候是省略方法名的，程序无法判定
                 *      重写的是哪一个接口
                 *  jdk1.8之后，提供了很多函数式接口作为参数传递
                 *  Consumer<T>:消费型接口   表示接受单个输入参数且不返回结果的操作。与大多数其他功能接口不同，Consumer期望通过副作用来操作。
                 * 这是一个函数式接口，其函数式方法是accept(Object)。
                 * Supplier<T>: 供给型接口   表示结果的提供者。不要求每次调用供应商时都返回一个新的或不同的结果。这是一个函数式接口，其函数式方法是get()。
                 * Function<T,R>: 函数型接口   对类型为T的对象应用操作，并返回结果，结果是R类型的对象，包含方法 R apply(T t)
                 * Predicate<T>:断言型接口  确定类型为T的对象是否满足某约束，并返回boolean值，包含方法 boolean test（T t）
                 *
                 * 方法引用：方法引用是lambda表达式的另一种写法，当要传递给lambda体的操作已经有实现的方法了，可以使用方法引用（省略参数）
                 * 注意实现抽象方法的参数列表，必须与方法引用方法的参数列表保持一致
                 * 对象::实例方法、类::静态方法、类::实例方法
                 *
                 * 构造引用
                 * Class::new
                 */
                .map(x->x+x)
               // .map(Integer::parseInt)
                        .forEach(x-> System.out.println(x));
//        System.out.println(stringStream);//java.util.stream.ReferencePipeline$3@22eeefeb  Pipeline
    }

    @Test
    public void test_mapfilterreduce(){//工厂
        var result = Stream.of(1, 2, 3, 4, 5, 6)
                //.map(x -> x.toString())
                .map(x->x*x)
                .filter(x->x<20)
                .reduce(Math::max);
                //.orElse(0);//累计器
                //.reduce(0,Math::max);//累计器
        System.out.println(result.isPresent());//是否有设置值
        System.out.println(result.get());//获取值
        System.out.println(result.orElseGet(()->0));
        //可以用lambda表达式来创建改接口的对象，@FunctionalInterface 注解，用来检查它是否是一个函数式接口
        MyInterFace<String> face = (x)-> System.out.println("test:"+x);
        face.getValue("777");
    }

    @Test
    public void test_flatMap(){//工厂
        //String->Stream<R>
         var set = Stream.of("my","Mine")
                .flatMap(str->str.chars().mapToObj(i->(char) i))
                //.forEach(System.out::println);
                 .collect(Collectors.toSet());
        System.out.println(set.stream().collect(Collectors.toList()));
    }

    @Test
    public void test_parallel(){
        var r = new Random();
        var list = IntStream.range(0,1_000_000)
                .map(x->r.nextInt(10_000_000))
                .boxed()
                .collect(Collectors.toList());
        var t0 = System.currentTimeMillis();
        System.out.println(list.stream().max((a,b)->a-b));
        System.out.println("time:"+(System.currentTimeMillis()-t0));
        var t1 = System.currentTimeMillis();
        System.out.println(list.parallelStream().max((a,b)->a-b));
        System.out.println(list.stream().parallel().max((a,b)->a-b));
        System.out.println("time2:"+(System.currentTimeMillis()-t1));
    }
}
//自定义一个函数式接口
@FunctionalInterface//此注解用来表明这是一个函数式接口
interface MyInterFace<T>{
    //函数式接口中只能有一个抽象方法
    void getValue(T t);
}
