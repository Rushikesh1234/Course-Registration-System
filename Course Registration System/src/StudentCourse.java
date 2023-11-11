import java.util.*;

public class StudentCourse {
    public static void main(String[] arg)
    {
        String[][] queries1 = {
            {"CREATE_COURSE", "CSE220", "System Programming", "3"},
            {"CREATE_COURSE", "CSE221", "System Programming", "4"},
            {"CREATE_COURSE", "CSE220", "Computer Architecture", "3"},
            {"CREATE_COURSE", "CSE300", "Introduction to Algorithms", "20"},
            {"CREATE_COURSE", "CSE330", "Operating Systems", "4"},
            {"REGISTER_FOR_COURSE", "st001", "CSE220"},
            {"REGISTER_FOR_COURSE", "st001", "CSE220"},
            {"REGISTER_FOR_COURSE", "st001", "CSE300"},
            {"REGISTER_FOR_COURSE", "st001", "CSE330"},
            {}
        };

        String[][] queries2 = {
            {"CREATE_COURSE", "CSE220", "System Programming", "3"},
            {"CREATE_COURSE", "CSE300", "Introduction to Algorithms", "3"},
            {"CREATE_COURSE", "CSE330", "Operating Systems", "4"},
            {"REGISTER_FOR_COURSE", "st002", "CSE220"},
            {"GET_PAIRED_STUDENTS"},
            {"REGISTER_FOR_COURSE", "st001", "CSE220"},
            {"REGISTER_FOR_COURSE", "st003", "CSE300"},
            {"REGISTER_FOR_COURSE", "st004", "CSE330"},
            {"REGISTER_FOR_COURSE", "st006", "CSE300"},
            {"GET_PAIRED_STUDENTS"},
            {"REGISTER_FOR_COURSE", "st005", "CSE300"},
            {"REGISTER_FOR_COURSE", "st003", "CSE330"},
            {"GET_PAIRED_STUDENTS"}
        };
        
        String[] result = executeQueries(queries2);

        for(String r : result)
        {
            System.out.println(r);
        }
    }

    public static String[] executeQueries(String[][] queries)
    {
        String[] result = new String[queries.length];

        Map<String, ArrayList<String>> course =  new TreeMap<>();
        Map<String, ArrayList<String>> student =  new TreeMap<>();

        for(int i=0; i<queries.length; i++)
        {
            String action = queries[i][0];
            if(action.equals("CREATE_COURSE"))
            {
                String courseId = queries[i][1];
                String courseName = queries[i][2];
                String courseCredit = queries[i][3];

                if(!course.containsKey(courseId))
                {
                    if(duplicateCourseName(course, courseName))
                    {
                        result[i] = "false";
                        continue;
                    }
                    else
                    {
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(courseName);
                        temp.add(courseCredit);

                        course.put(courseId, temp);
                        result[i] = "true";
                    }
                }
                else
                {
                    result[i] = "false";
                    continue;
                }

            }
            else if(action.equals("REGISTER_FOR_COURSE"))
            {
                String studentId = queries[i][1];
                String courseId = queries[i][2];

                if(!student.containsKey(studentId))
                {
                    if(checkCourseNotAvailable(course, courseId) || checkCreditLimitExceedForFirstCourse(course, courseId))
                    {
                        result[i] = "false";
                        continue;
                    }
                    else
                    {
                        ArrayList<String> courseList = new ArrayList<>();
                        courseList.add(courseId);
                        student.put(studentId, courseList);
                        result[i] = "true";
                    }
                }
                else
                {
                    if(checkDuplicateCourse(student, studentId, courseId) || checkCreditLimitExceed(course, student, studentId, courseId))
                    {   
                        System.out.print("Error for "+studentId);
                        result[i] = "false"+studentId;
                        continue;
                    }
                    else
                    {
                        ArrayList<String> courseList = student.get(studentId);
                        courseList.add(courseId);
                        student.put(studentId, courseList);
                        result[i] = "true";
                    }
                }
            }
            else if(action.equals("GET_PAIRED_STUDENTS"))
            {
                String pairs = findPair(course, student);
                result[i] = pairs;
            }
            /*
            System.out.println("At Loop "+i);
            System.out.println("Course");
            System.out.println(course);
            System.out.println("Student");
            System.out.println(student);
            System.out.println("Result is "+ result[i]);
            */
        }

        return result;
    }

    public static boolean duplicateCourseName(Map<String, ArrayList<String>> course, String courseName)
    {
        for(Map.Entry<String, ArrayList<String>> entry : course.entrySet())
        {
            if(entry.getValue().get(0).equals(courseName))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean checkCourseNotAvailable(Map<String, ArrayList<String>> course, String courseId)
    {
        for(Map.Entry<String, ArrayList<String>> entry : course.entrySet())
        {
            String subject = entry.getKey();
            if(subject.equals(courseId))
            {
                return false;
            }
        }

        return true;
    }
    
    public static boolean checkCreditLimitExceedForFirstCourse(Map<String, ArrayList<String>> course, String courseId)
    {
        int currentCourseCredit = Integer.parseInt(course.get(courseId).get(1));
        if(currentCourseCredit > 24)
        {
            return true;
        }
        return false;
    }
    public static boolean checkCreditLimitExceed(Map<String, ArrayList<String>> course, Map<String, ArrayList<String>> student, String studentId, String courseId)
    {
        int currentCourseCredit = Integer.parseInt(course.get(courseId).get(1));
        if(currentCourseCredit > 24)
        {
            return true;
        }

        int total = 0;
        ArrayList<String> courseList = student.get(studentId);

        for(String c : courseList)
        {
            total += Integer.parseInt(course.get(c).get(1));
        }

        if(total+currentCourseCredit > 24)
        {
            return true;
        }

        return false;
    }

    public static boolean checkDuplicateCourse(Map<String, ArrayList<String>> student, String studentId, String courseId)
    {
        ArrayList<String> courseList = student.get(studentId);

        for(String c : courseList)
        {
            if(c.equals(courseId))
            {
                return true;
            }
        }

        return false;
    }

    public static String findPair(Map<String, ArrayList<String>> course, Map<String, ArrayList<String>> student)
    {
        Map<String, ArrayList<String>> studentCourseList = new TreeMap<>();

        for(Map.Entry<String, ArrayList<String>> entry : student.entrySet())
        {
            String studentId = entry.getKey();
            ArrayList<String> courses = entry.getValue();

            for(String c : courses)
            {
                if(!studentCourseList.containsKey(c))
                {
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(studentId);
                    studentCourseList.put(c, temp);
                }
                else
                {
                    ArrayList<String> temp = studentCourseList.get(c);
                    temp.add(studentId);
                    studentCourseList.put(c, temp);
                }
            }
        }
        // System.out.println(studentCourseList);


        List<List<String>> studentPairs = new ArrayList<>();

        for(Map.Entry<String, ArrayList<String>> entry : studentCourseList.entrySet())
        {
            List<String> students = entry.getValue();

            if(students.size() == 1)
            {
                continue;
            }
            else
            {
                for(int i=0; i<students.size()-1; i++)
                {
                    for(int j=i+1; j<students.size(); j++)
                    {
                        String studentA = students.get(i);
                        String studentB = students.get(j);

                        studentPairs.add(List.of(studentA, studentB));
                    }
                }
            }
        }

        if(studentPairs.size() == 0)
        {
            return "";
        }

        Collections.sort(studentPairs, (pair1, pair2) -> {
            int compareFirst = pair1.get(0).compareTo(pair2.get(0));
            if (compareFirst != 0) {
                return compareFirst;
            }
            return pair1.get(1).compareTo(pair2.get(1));
        });
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(List<String> s : studentPairs)
        {
            String a = s.get(0);
            String b = s.get(1);

            String pair = "["+a+", "+b+"]";

            sb.append(pair);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");

        return sb.toString();
    }
}
