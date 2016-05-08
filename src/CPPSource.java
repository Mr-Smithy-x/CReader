import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cj on 5/7/16.
 */
public class CPPSource {

    private HashMap<String, String> classes;
    private HashMap<String, String> functions;
    private HashMap<String, ArrayList<String>> headers;

    public CPPClassObject getClass(String className){
        if(className.contains(className)){
            return CPPClassObject.Parse(className, classes.get(className));
        }else return null;
    }

    public CPPFunctionObject getFunction(String functionName){
        if(functions.containsKey(functionName)){
            return CPPFunctionObject.Parse(functionName, functions.get(functionName));
        }else return null;
    }

    /**
     * Parses the main C++ Source code and split them.
     * @param file
     * @return CPPSource
     * @throws IOException
     * @see CPPSource
     */
    public static CPPSource parse(String file) throws IOException {
        File f = new File(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String stub = null;
        StringBuilder sb = new StringBuilder();
        while ((stub = br.readLine())!=null){
            sb.append(stub + "\n");
        }
        br.close();
        return _parse(sb.toString());
    }

    //region Classes
    public static class CPPClassObject {

        HashMap<String, String> functions = new HashMap<>();
        HashMap<String, String> constructor =  new HashMap<>();
        List<String> fields = new ArrayList<>();


        private void ParseFunctions(String cpp, String clazzName){
            int delim = 0;
            boolean entered = false;
            int first = -1;
            int second = -1;
            String[] c = new String[cpp.split("\n").length-2];
            Arrays.asList(cpp.split("\n")).subList(1,cpp.split("\n").length-1).toArray(c);
            for(int i = 0; i < c.length; i++){
                if(c[i].contains(";") && !c[i].contains("(") && !c[i].contains("->") && !c[i].contains(".") && !c[i].contains("return")){
                    String[] old = c[i].trim().replaceAll(",","").replaceAll(";","").split(" ");
                    String[] values = new String[old.length-1];
                    Arrays.asList(old).subList(1, old.length).toArray(values);
                    for(String s : values){
                        fields.add(s);
                    }
                }
                if(c[i].contains("{")) {
                    delim++;
                    entered = true;
                    if(first == -1){
                        first = i;
                    }
                }
                if(c[i].contains("}")) {
                    delim--;
                    if(delim == 0 && entered){
                        entered = false;
                        second = i;
                    }
                }
                if(first != -1 && second != -1 && second > first){
                    List<String> a  = Arrays.asList(c).subList(first,second+1);
                    StringBuilder sb = new StringBuilder();
                    for(String b : a){
                        sb.append(b + "\n");
                    }
                    String beg = a.get(0).trim();
                    if(!beg.startsWith(clazzName) && !beg.contains("class") && beg.contains("(")){
                        String name = beg.split(" ")[1];
                        name = name.substring(0,name.indexOf("("));
                        functions.put(name, sb.toString());
                    }else if(beg.startsWith(clazzName) && !beg.contains("class") && beg.contains("(")){
                        String name = beg.substring(0,beg.indexOf("("));
                        constructor.put(name, sb.toString());
                    }
                    first = -1;
                    second = -1;
                }
            }
        }

        public String getFunction(String functionName){
            if(functions.containsKey(functionName)){
                return functions.get(functionName);
            }else return null;
        }

        public static CPPClassObject Parse(String clazzName, String clazz){
            CPPClassObject cppClassObject = new CPPClassObject();
            cppClassObject.ParseFunctions(clazz,clazzName);
            return cppClassObject;
        }
    }

    private static class CPPFunctionObject {
        List<CPPParameter> cppParameterList;
        public static CPPFunctionObject Parse(String functionName, String functionSource){
            ParseFunction(functionSource);
            return null;
        }

        private static void ParseFunction(String functionSource) {

        }
    }

    private static class CPPParameter {
        String type, name, value;
        private CPPParameter(){}
        public static CPPParameter Builder(){
            return new CPPParameter();
        }
        public String getType() {
            return type;
        }

        public CPPParameter setType(String type) {
            this.type = type;
            return this;
        }

        public String getName() {
            return name;
        }

        public CPPParameter setName(String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return value;
        }

        public CPPParameter setValue(String value) {
            this.value = value;
            return this;
        }
    }
    //endregion

    //region Parse MainSource
    private CPPSource(HashMap<String, String> classes, HashMap<String, String> functions, HashMap<String, ArrayList<String>> headers){
        this.classes = classes;
        this.functions = functions;
        this.headers = headers;
    }

    private static HashMap<String, ArrayList<String>> getHeaders(String cpp){
        HashMap<String, ArrayList<String>> headers = new HashMap<>();
        for(String s : cpp.split("\n")){
            if(s.startsWith("#")){
                String hash = s.substring(1).split(" ")[0];
                if(!headers.containsKey(hash)) headers.put(hash, new ArrayList<String>());
                headers.get(hash).add(s.substring(1).split(" ")[1]);
            }
        }
        return headers;
    }

    private static void parseFunctionsAndClasses(String cpp, HashMap<String,String> classes, HashMap<String, String> functions){
        int delim = 0;
        boolean entered = false;
        int first = -1;
        int second = -1;
        String[] c = cpp.split("\n");
        for(int i = 0; i < c.length; i++){
            if(c[i].contains("{")) {
                delim++;
                entered = true;
                if(first == -1){
                    first = i;
                }
            }
            if(c[i].contains("}")) {
                delim--;
                if(delim == 0 && entered){
                    entered = false;
                    second = i;
                }
            }
            if(first != -1 && second != -1 && second > first){
                List<String> a  = Arrays.asList(c).subList(first,second+1);
                StringBuilder sb = new StringBuilder();
                for(String b : a){
                    sb.append(b + "\n");
                }
                String beg = a.get(0);
                if(beg.contains("class")){
                    String name = beg.split(" ")[1];
                    name = name.substring(0,name.indexOf("{"));
                    classes.put(name, sb.toString());
                }
                if(!beg.contains("class")){
                    String name = beg.split(" ")[1];
                    name = name.substring(0,name.indexOf("("));
                    functions.put(name, sb.toString());
                }
                first = -1;
                second = -1;
            }
        }
    }

    private static CPPSource _parse(String cpp) {
        HashMap<String, ArrayList<String>> headers = getHeaders(cpp);
        HashMap<String, String> classes = new HashMap<>();
        HashMap<String, String> functions = new HashMap<>();
        parseFunctionsAndClasses(cpp, classes, functions);
        System.out.println(String.format("There are %s class(es)", classes.size()));
        System.out.println(String.format("There are %s function(s)",functions.size()));
        System.out.println(String.format("There are %s header(s)",headers.size()));
        return new CPPSource(classes,functions,headers);
    }
    //endregion
}
