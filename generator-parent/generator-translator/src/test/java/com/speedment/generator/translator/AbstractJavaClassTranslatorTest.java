package com.speedment.generator.translator;

import com.speedment.common.codegen.model.*;
import com.speedment.common.codegen.model.Class;
import com.speedment.runtime.config.Dbms;
import com.speedment.runtime.config.Document;
import com.speedment.runtime.config.Project;
import com.speedment.runtime.config.Schema;
import com.speedment.runtime.config.internal.BaseDocument;
import com.speedment.runtime.config.internal.SchemaImpl;
import com.speedment.runtime.config.trait.HasId;
import com.speedment.runtime.config.trait.HasMainInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AbstractJavaClassTranslatorTest {


    private static final String MY_PROJECT_NAME = "myProject";

    private Map<String, AtomicInteger> visitedMap;

    @BeforeEach
    void setup() {
        visitedMap = new ConcurrentHashMap<>();
    }

    private <D extends HasId & HasMainInterface> void visit(D doc) {
        visitedMap.computeIfAbsent(keyFor(doc.mainInterface(), doc.getId()), $ -> new AtomicInteger()).incrementAndGet();
    }

    private <D extends Document> String keyFor(java.lang.Class<D> clazz, String id) {
        return clazz.getSimpleName() + "#" + id;
    }

    @Test
    void testEmptyProjectForEveryProject() {
        testWithEmptyProject(b ->
            b.forEveryProject((clazz, p) -> {
                visit(p);
            }),
            () -> assertVisitedMapEquals(keyFor(Project.class, MY_PROJECT_NAME), 1)
        );
    }

    @Test
    void testEmptyProjectForEveryDbms() {
        testWithEmptyProject(b ->
            b.forEveryDbms((clazz, d) -> {
                visit(d);
            }),
            this::assertVisitedMapEquals
        );
    }

    @Test
    void testProjectWihtOneDbmsForEveryProject() {
        testWitOneDbms(b ->
                b.forEveryProject((clazz, p) -> {
                    visit(p);
                }),
            () -> assertVisitedMapEquals(keyFor(Project.class, MY_PROJECT_NAME), 1)
        );
    }


    @Test
    void testProjectWihtOneDbmsForEveryDbms() {
        testWitOneDbms(b ->
                b.forEveryDbms((clazz, d) -> {
                    visit(d);
                }),
            () -> assertVisitedMapEquals(keyFor(Dbms.class, "dbms0"), 1)
        );
    }

    @Test
    void testProjectWihtTwoDbmsesForEveryDbms() {
        testWitTwoDbms(b ->
                b.forEveryDbms((clazz, d) -> {
                    visit(d);
                }),
            () -> assertVisitedMapEquals(
                entry(keyFor(Dbms.class, "dbms0"), 1),
                entry(keyFor(Dbms.class, "dbms1"), 1)
            )
        );
    }


    private void testWithEmptyProject(UnaryOperator<Translator.Builder<Class>> operator, Runnable assertor) {
        MyProject project = new MyProject();
        MyTranslator translator = new MyTranslator(project, operator);
        MyFile file = new MyFile();
        translator.makeCodeGenModel(file);
        assertor.run();
    }

    private void testWitOneDbms(UnaryOperator<Translator.Builder<Class>> operator, Runnable assertor) {
        Map<String, Object> doc = new HashMap<>();
        doc.put(HasId.ID, MY_PROJECT_NAME);
        addDbms(doc, "dbms0");
        MyProject project = new MyProject(doc);
        MyTranslator translator = new MyTranslator(project, operator);
        MyFile file = new MyFile();
        translator.makeCodeGenModel(file);
        assertor.run();
    }

    private void testWitTwoDbms(UnaryOperator<Translator.Builder<Class>> operator, Runnable assertor) {
        Map<String, Object> doc = new HashMap<>();
        doc.put(HasId.ID, MY_PROJECT_NAME);
        addDbms(doc, "dbms0");
        addDbms(doc, "dbms1");
        MyProject project = new MyProject(doc);
        MyTranslator translator = new MyTranslator(project, operator);
        MyFile file = new MyFile();
        translator.makeCodeGenModel(file);
        assertor.run();
    }


    private void addDbms(Map<String, Object> map, String dbmsId) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> list = (List<Map<String, Object>>)map.get(Project.DBMSES);
        if (list == null) {
            list = new ArrayList<>();
            map.put(Project.DBMSES, list);
        }
        list.add(mapWithId(dbmsId));
    }

    private static final class MyTranslator extends AbstractJavaClassTranslator<Project, Class> {

        private final UnaryOperator<Builder<Class>> operator;

        private MyTranslator(Project project, UnaryOperator<Builder<Class>> operator) {
            super(project, Class::of);
            this.operator = requireNonNull(operator);

        }

        @Override
        protected String getClassOrInterfaceName() {
            return "MyClass";
        }

        @Override
        protected Class makeCodeGenModel(File file) {
            Translator.Builder<Class> builder = newBuilder(file, getClassOrInterfaceName());
            builder = operator.apply(builder);
            return builder.build();
        }

        @Override
        protected String getJavadocRepresentText() {
            return null;
        }
    }


    private static final class MyProject extends BaseDocument implements Project {

        private MyProject(String id) {
            this(mapWithId(id));
        }

        private MyProject() {
            this(MY_PROJECT_NAME);
        }

        private MyProject(Map<String, Object> data) {
            super(null, data);
        }

        @Override
        public String getName() {
            // Must implement getName because Project does not have any parent.
            return getAsString(NAME).orElse(DEFAULT_PROJECT_NAME);
        }

        @Override
        public Optional<? extends Document> getParent() {
            return Optional.empty();
        }

        @Override
        public Stream<Document> ancestors() {
            return Stream.empty();
        }

        @Override
        public Stream<? extends Dbms> dbmses() {
            return children(DBMSES, MyDbmsImpl::new);
        }
    }

    final static class MyDbmsImpl extends BaseDocument implements Dbms {

        private MyDbmsImpl(Project parent, Map<String, Object> data) {
            super(parent, data);
        }

        @Override
        public Stream<? extends Schema> schemas() {
            return children(SCHEMAS, SchemaImpl::new);
        }

        @Override
        public Optional<Project> getParent() {
            @SuppressWarnings("unchecked")
            final Optional<Project> parent = (Optional<Project>) super.getParent();
            return parent;
        }

    }

    private static class MyFile implements File {

        private String name;

        @Override
        public List<ClassOrInterface<?>> getClasses() {
            return null;
        }

        @Override
        public File copy() {
            return this;
        }

        @Override
        public List<Import> getImports() {
            return emptyList();
        }

        @Override
        public File set(Javadoc doc) {
            return this;
        }

        @Override
        public Optional<Javadoc> getJavadoc() {
            return Optional.empty();
        }

        @Override
        public File setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private void assertVisitedMapEquals(String key, Integer value) {
        assertVisitedMapEquals(entry(key, value));
    }

    private void assertVisitedMapEquals() {
        assertVisitedMapEquals(emptyMap());
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    private final void assertVisitedMapEquals(Map.Entry<String, Integer>... entries) {
        assertVisitedMapEquals(
            Stream.of(entries)
                .collect(
                    toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                    )
                )
        );
    }

    private static Map<String, Object> mapWithId(String id) {
        return Stream.<Map.Entry<String, Object>>of(
            new AbstractMap.SimpleEntry<>(HasId.ID, requireNonNull(id))
        ).collect(
            toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            )
        );
    }


    private void assertVisitedMapEquals(Map<String, Integer> map) {
        assertEquals(map.size(), visitedMap.size(), "The size of the maps differ");
        map.forEach((k, v) -> {
            assertTrue(visitedMap.containsKey(k), "Expected: " + map + ", Actual: " + visitedMap);
            assertEquals((int) v, visitedMap.get(k).get());
        });
    }

    private <T> UnaryOperator<T> compose(UnaryOperator<T> first, UnaryOperator<T> second) {
        return (T t) -> second.apply(first.apply(t));
    }

    private static <K, V> AbstractMap.Entry<K, V> entry(K k, V v) {
        return new AbstractMap.SimpleEntry<>(k, v);
    }



}