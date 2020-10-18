package TestsWeka;

import TestsWeka.classes.FooClass;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Test2 {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        // testAddFieldWithByteBuddy2();
        testDeclare();
    }


    public static void testAddFieldWithByteBuddy2() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> newClass = new ByteBuddy().rebase(FooClass.class)
                .defineField("foo", String.class, Visibility.PRIVATE)
                .make()
                .load(ClassLoader.getSystemClassLoader())
                .getLoaded();


    }


    public static void testDeclare() throws IllegalAccessException, InstantiationException {

        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/HAL5.drl", getMyRuleDeclare());

        KieBuilder kb = ks.newKieBuilder(kfs);

        kb.buildAll(); // kieModule is automatically deployed to KieRepository if successfully built.
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        KieSession kSession = kContainer.newKieSession();
        kSession.setGlobal("out", System.out);

        FactType testClass = kContainer.getKieBase().getFactType("TestsWeka", "TestClass");

        Object first = testClass.newInstance();
        testClass.set(first, "myField", "Hello, HAL. Do you read me, HAL?!");

        kSession.insert(first);
        kSession.fireAllRules();
        Object gdffdg = kSession.getGlobal("out");
        System.out.println(gdffdg.toString());


    }

//package test.droolsTest.createClass.TestMessage

    private static String getMyRuleDeclare() {
        String s =
                "global java.io.PrintStream out \n\n" +
                        "declare TestClass\n" +
                        "  myField : String\n" +
                        "end\n\n";

        return s;
    }


}
