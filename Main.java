import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.*;



//------------------------------------------------------------------------------------
// error - account type , intermediary in equity
//------------------------------------------------------------------------------------

//This class is for debugging and printing
class Debug {
    public static void list_arr_list(ArrayList<String[]> arr){
        System.out.println("-----------------------");
        for(String s[] : arr){
            for(String ele : s){
                System.out.print(ele + " | ");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
    }
    public static void list_arr(ArrayList<String> arr){
        System.out.println("-----------------------");
        for(String ele : arr){

            System.out.print(ele + " | ");
        }
        System.out.println("-----------------------");
    }

}


//This class is able to make csv from credentials of users
class Credentials{
//    this function return substring after removing and replacing unwanted values
    public static String get_substring(String[] final_data,int start,int end){
        ArrayList<String> temp_arr  = new ArrayList<>();
        for(int i=start;i<end;i++){
            String value = final_data[i];
            value = value.replaceAll("\r", " ").replaceAll("\n"," ");
            temp_arr.add(value);
        }
        String ans = "";
        for(String s: temp_arr){
            ans+=s;
        }

        return ans;
    }
    public static void make_csv(ArrayList<String> final_data,String path) throws FileNotFoundException {
        String name = "Credentials"+".csv";
        // make file from giving path
        File file = new File(path+name);
        try{
            // export file
            FileWriter outputfile = new FileWriter(file);
            // convert normal file to csv
            CSVWriter writer = new CSVWriter(outputfile);
            // supply format for header
            String[] header_template = {"NSDL ID", "NAME", "ADDRESS" , "PINCODE" , "DATE OF BIRTH", "REGISTERED EMAIL", "AADHAAR NUMBER","MOBILE NUMBER", "PAN NUMBER"};
            // write format for csv
            writer.writeNext(header_template);

            String[] value =  {final_data.get(0),final_data.get(1),final_data.get(2),final_data.get(3),final_data.get(4),final_data.get(5),final_data.get(6),final_data.get(7),final_data.get(8)};
            writer.writeNext(value);



            // close  file stream
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    // get section one data which included  - nsdl id , name ,address , pincode
    public static String[] get_section_one_data(String data){
        Pattern p_start = Pattern.compile("NSDL ID");//. represents single character
        Matcher m_start = p_start.matcher(data);
        boolean b_start = m_start.find();
        String ans ="";
        String[] arr = new String[0];
        Pattern p_end = Pattern.compile("YOUR CONSOLIDATED");//. represents single character
        Matcher m_end = p_end.matcher(data);
        boolean b_end = m_end.find();

        // if there is section one data 's
        if(b_start && b_end){
            int pointer = m_start.start();
            int stop_pointer = m_end.start();
            for(int i=pointer;i<stop_pointer;i++){
                ans += data.charAt(i);
            }

// here will split all the datas by new line between nsdl id to pincode
            arr  = ans.split("\n");
        }
        return arr;
    }

//    this fuction returns dob
    public static String get_dob(String data){
        Pattern p = Pattern.compile("(DD/MM/YYYY)");//. represents single character
        Matcher m = p.matcher(data);
        boolean b = m.find();
        if(b){
            String dob = "";
            for(int i=m.end()+1;i<=m.end()+12;i++){
                dob+=data.charAt(i);
            }
            return dob;
        }
        return "NA";
    }
//    this function returns email
    public static String get_email(String data){
        String regex = "([a-z0-9_.-]+)@([a-z0-9_.-]+[a-z])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(data);
        boolean b = m.find();
        if(b){
            return data.substring(m.start(),m.end());
        }
        return "NA";
    }
//    this function returns adharcard number
    public static String get_adhar_num(String data){
        String patt= "\\d{4}\\s\\d{4}\\s\\d{4}\\s\\d{4}\\s";
        Pattern p = Pattern.compile(patt);
        Matcher m = p.matcher(data);
        boolean b = m.find();
        if(b){
            return data.substring(m.start(),m.end());

        }
        return "NA";
    }
//    This function returns mobile number
    public static String get_mobile_num(String data){
        String pat = "REGISTERED MOBILE";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(data);
        boolean b = m.find();
        if(b){
            String mobile_num = "";
            for(int i =m.end()+1;i<=11+m.end();i++){
                mobile_num+=data.charAt(i);
            }
            return mobile_num;
        }
        return "NA";
    }
//    This function returns pancard number
    public static String get_pan_acc_number(String data){
        String pat = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(data);
        boolean b = m.find();
        if(b){
            return data.substring(m.start(),m.end());
        }

        return "NA";
    }

//    get section two data which included -  dob , email , adhar num , mobile no, pancard num
    public static String[] get_section_two_data(String data){
        String dob = get_dob(data);
        String email = get_email(data);
        String adhar_number = get_adhar_num(data);
        String mobile_number = get_mobile_num(data);
        String pan_acc_num = get_pan_acc_number(data);

        String[] result = new String[5];
        result[0] = dob;
        result[1] = email;
        result[2] = adhar_number;
        result[3] = mobile_number;
        result[4] = pan_acc_num;

        return result;
    }

//    this function seperates the paragraph into partition and return array of string
    public static String[] clean_section_one_data(String[] data){
        String[] result = new String[4];
        result[0] = clean_nsid(data[0]);
        result[1] = data[1];
        result[2] = get_substring(data,2, data.length-1);
        result[3] = clean_pincode(data[data.length-1]);
        return result;
    }

//    This function removes the heading like
//    nsdl id - xxxxxxxxx so it removes nsdl id and return xxxxxxxxxx
    public static String clean_nsid(String nsid){
        int pointer = nsid.length()-1;
        String ans = "";
        while(pointer>=0){
            char curr_char = nsid.charAt(pointer);
            if(Character.isDigit(curr_char)){
                ans+=curr_char;
            }
            pointer--;

        }
        ans = new StringBuffer(ans).reverse().toString();
        return ans;
    }

//    This function removes the heading like
//    pincode - xxxxxxxxx so it removes pincode and return xxxxxxxxxx
    public static String clean_pincode(String pincode){
        int pointer = pincode.length()-1;
        String ans = "";
        while(pointer>=0){
            char curr_char = pincode.charAt(pointer);
            if(Character.isDigit(curr_char)){
                ans+=curr_char;
            }
            pointer--;

        }
        ans = new StringBuffer(ans).reverse().toString();
        return ans;
    }

//    this function makes array after joining two arrays for building csv
    public static ArrayList<String> make_final_arr(String[] arr1, String[] arr2){
        ArrayList<String> arr = new ArrayList<>();
        for(String value: arr1){
            value = value.replaceAll("\r", " ").replaceAll("\n"," ");
            arr.add(value);
        }
        for(String value:arr2){
            value = value.replaceAll("\r", " ").replaceAll("\n"," ");
            arr.add(value);
        }
        for(int i=0;i<arr.size();i++){
            arr.set(i,arr.get(i).trim());
        }
        return arr;
    }


// this method is get all the individual data and make csv from it
    public static void fetch_user_info(String data,String path) throws FileNotFoundException {
        String[] section_one_data = get_section_one_data(data);
        String[] final_one_section =  clean_section_one_data(section_one_data);
        String[] section_two_data = get_section_two_data(data);
        ArrayList<String> final_data = make_final_arr(final_one_section,section_two_data);

        make_csv(final_data,path);


    }

}

//This class is able to fetch data for holdings from pdf
class Holdings{
//    this function parse the string and apply pattern to find holdings
    public static ArrayList<String[]> clean_holding_data(String text){
//        this pattern for fetching all  the holding by it's starting DEC, JAN ....
        Pattern p = Pattern.compile("[A-Z]{3}\\s(\\d){4}");
        Matcher m = p.matcher(text);

//        this function for ending the pattern
        Pattern p_end= Pattern.compile("Summary of value of holdings of");
        Matcher m_end = p_end.matcher(text);

        int end = 0;
        if(m_end.find()){
            end = m_end.start();
        }
        ArrayList<Integer> index_arr = new ArrayList<>();
        ArrayList<String[]> result = new ArrayList<>();

        while(m.find()){
            index_arr.add(m.start());
        }
        for(int start : index_arr){
            if(start<end){
                String cur_ans = "";
                for(int i=start;i<text.length();i++){
                    String s = "" + text.charAt(i);
                    if(s.equals("\n")){
                        break;
                    }
                    else{
                        cur_ans += s;
                    }
                }
                cur_ans = cur_ans.replaceAll("\r", " ").replaceAll("\n"," ");
                String[] string_list = cur_ans.split(" ");
                result.add(string_list);
            }
        }
        return result;

    }
//    this function is able to generate csv from arraylist of values
    public static void make_csv(ArrayList<String[]> values , String path){

        String name = "Holdings"+".csv";


        // make file from giving path
        File file = new File(path+name);
        try{
            // export file
            FileWriter outputfile = new FileWriter(file);
            // convert normal file to csv
            CSVWriter writer = new CSVWriter(outputfile);
            // supply format for header
            String[] header_template = {"MONTHS","CONSOLIDATED PORTFOLIO VALUE (Rs)","CHANGE (Rs)" , "CHANGE (%)"};
            // write format for csv
            writer.writeNext(header_template);

            for(int i=0;i<values.size();i++){
                String[] arr = values.get(i);
                writer.writeNext(arr);
            }

            // close  file stream
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
//    this method takes string and make csv
    public static void fetch_data(String text,String path){
        ArrayList<String[]> values  = clean_holding_data(text);
        make_csv(values,path);
    }
}

//This class is able to fetch data for composition from pdf
class Composition{

//    This method takes paragraph and make partition of data's
    public static String[] get_partition(String text){
        String asset_class = "";
        int value_start = 0;
        for(int i=0;i<text.length();i++){
            char value = text.charAt(i);
            if(value==')'){
                value_start = i;
                asset_class+=value;
                break;
            }
            else{
                asset_class+=value;
            }
        }
        String last_two = text.substring(value_start+1,text.length()).trim();
        String[] arr_last_two  = last_two.split(" ");

        String[] res = new String[3];
        res[0] = asset_class.isEmpty()? "NA" : asset_class;
        res[1] = arr_last_two[0].replaceAll(",","").isEmpty()? "NA" :arr_last_two[0].replaceAll(",","");
        res[2] = arr_last_two[1].isEmpty()? "NA" : arr_last_two[1] ;
        return res;
    }
    public static void make_csv(String[] arr, String path){
        String name = "Composition"+".csv";

        File file = new File(path+name);
        try{
            FileWriter output = new FileWriter(file);
            CSVWriter csvWriter = new CSVWriter(output);

            String[] header = {"ASSET CLASS","VALUE IN (Rs.)", "Percentage %"};
            csvWriter.writeNext(header);
            for(int i=1;i<arr.length-2;i++){
                String[] res = get_partition(arr[i]);
                csvWriter.writeNext(res);
            }
            csvWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
//    This function takes the start and find nearest ending pattern so we don't have to deal with unnecessory data
    public static int get_min_dist(String text,int start) {
        Pattern p = Pattern.compile("NSDL Demat Account");
        Matcher m = p.matcher(text);
        int min = Integer.MAX_VALUE;
        int end = 0;
        while (m.find()) {
            int dist = m.start() - start ;
            if (dist >= 0) {
                if (dist <= min) {
                    min = dist;
                    end = m.start();
                }
            }
        }
        return end;
    }
//    This function makes partition  of data which we want by applying pattern
    public static String get_perticular_data(String text){

        Pattern p = Pattern.compile("ASSET CLASS Value in");
        Matcher m = p.matcher(text);
        boolean b = m.find();

        if(b){
            int start = m.start();
            int end = get_min_dist(text,start);
            return text.substring(start,end);
        }
        return "NA";
    }
//    This method takes the text clean and make csv
    public static void fetch_data(String text,String path){
        String data = get_perticular_data(text);
        String[] arr = data.split("\n");
        make_csv(arr,path);
    }
}

//This class is able to fetch data for lifeinsurance from pdf
class LifeInsurance{
//    This method is just like get_min_dist_from_start but here we take nearest from respect to  end rather than start
    public static int get_min_dist_from_end(String text,int end){
        Pattern p = Pattern.compile("Assured `");
        Matcher m = p.matcher(text);
        int min = Integer.MAX_VALUE;
        int start = 0;
        while(m.find()){
            int dist = end - m.start();
            if(dist>=0){
                if(dist<=min){
                    min = dist;
                    start = m.end();
                }
            }
        }
        return start;
    }

//    This method simly takes string and split by newline
    public static String[] get_partition(String arr){
        return arr.split(" ");
    }

//    This method takes path and arr and make csv
    public static void make_csv(String[] arr,String path){
        String name = "LifeInsurance"+".csv";

        File file = new File(path+name);
        try{
            FileWriter output = new FileWriter(file);
            CSVWriter csvWriter = new CSVWriter(output);
            String[] header = {"LIFE ASSURED","INSURANCE COMPANY NAME", "POLICY NAME","POLICY NUMBER","STATUS","PREMIUM INSTALLMENT (Rs) ","PREMIUM FREQUENCY","SUM ASSURED (Rs)"};
            csvWriter.writeNext(header);
            if(arr.length>0){
                for(int i=0;i<arr.length;i++){
                    String[] res = get_partition(arr[i]);
                    csvWriter.writeNext(res);
                }
            }
            csvWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

//    This method takes start and find the nearest ending point so that we don't have irrelevant data
    public static int get_min_dist_from_start(String text,int start) {
        Pattern p = Pattern.compile("Transactions");
        Matcher m = p.matcher(text);
        int min = Integer.MAX_VALUE;
        int end = 0;
        while (m.find()) {
            int dist = m.start() - start ;
            if (dist >= 0) {
                if (dist <= min) {
                    min = dist;
                    end = m.start();
                }
            }
        }
        return end;
    }

//     This method is to make partition from partition
//    if we have paragraph and in that paragraph we want the only perticular data till some point x that point is mid which  we returns here
    public static int get_mid(String text,int end){
        int mid = get_min_dist_from_end(text,end);
        return mid;
    }

//    From whole string this method makes partition for life insurance table
    public static String get_perticular_data(String text){
        String pat = "LIFE INSURANCE POLICIES";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        if(b){
            int start = m.start();
            int end = get_min_dist_from_start(text,start);
            int mid = get_mid(text,end);
            String data = text.substring(mid,end);
            return data;
        }
        return "NA";
    }

//    This method takes whole string and path and clean after make csv
    public static void fetch_data(String text,String path){
        String data= get_perticular_data(text);
        String[] arr = data.split("\n");
        make_csv(arr,path);
    }
}

//This class is able to fetch data for accounts from pdf
class Accounts{

    ArrayList<String[]> data = new ArrayList<>();
//    This method takes paragraph which is meaning full and out of that it takes element
    public static ArrayList<String[]> clean_paragraph(String para){
        String[] by_nl = para.split("\n");
        ArrayList<String> arr = new ArrayList<>();
        for(int i=2;i<by_nl.length;i++){
            String curr_str = by_nl[i];
            arr.add(curr_str);
        }


//        last for folios if there are folios
        String folios_para = arr.get(arr.size()-1);
        folios_para = folios_para.replace("\n"," ").replace("\r", " ");
        int length = arr.size();
        boolean is_folio_exist = false;
//        if there is folio then we have to treat folio separetly so we don't go to last we exclude last

        if(folios_para.contains("Folios")){
            is_folio_exist = true;
            length--;
        }


//        here in partition i observe that the even indexes 0 ,2,4.. has names of account type and account details which we separate by account
//        here in partition odd indexes 1,3,5 has the dpid,clientid,numisin,value
//        the loop is till last element because the last element is mutual funds folio which break the our logic so will implement seperately


        ArrayList<String> acc_type_detail = new ArrayList<>();
        //      0-> account type
        //      1-> account details

        ArrayList<String> dp_client_value = new ArrayList<>();
        //      2-> dp id
        //      3-> client id
        //      4-> value

        for(int i=0;i<length;i++){
            String current_string = arr.get(i);
//            even
            if((i&1)==0){
//                if even thene there is only account type and account details which separate by account
                String seperate_str = "Account";
                int n = seperate_str.length();
                int account_type_end = current_string.indexOf(seperate_str);
                String account_type = current_string.substring(0,account_type_end+n).replace("\r"," ").replace("\n"," ");
                String account_details = current_string.substring(account_type_end+n,current_string.length()).replace("\r"," ").replace("\n"," ");
                acc_type_detail.add(account_type+"-"+account_details);
            }
//            odd
            else{
//                here what we do is we seperate the string by spaces so
//                original = DP ID:14343225 CLIENT ID:239599 8 8,24105.23
//                seperat = [DP,ID:32524662,CLIENT,ID:29476439,8,8226436]
//                here we only want odd indexes
                String[] partition_arr = current_string.split(" ");
                String dp_id = partition_arr[1].replaceAll("\r"," ").replace("\n"," ");
                String client_id = partition_arr[3].replaceAll("\r"," ").replace("\n"," ");
                String value = partition_arr[partition_arr.length-1].replaceAll("\r"," ").replace("\n"," ");

                dp_id = dp_id.split(":")[1];
                client_id = client_id.split(":")[1];
                dp_client_value.add(dp_id+"-"+client_id+"-"+value);
            }
        }

        ArrayList<String[]> result= new ArrayList<>();

//        now out array list of acc type details  - (acc type - acc details )
//        and array list of dp client vlaue - (dp id- clientid - value)
//        we alternately take and merge it to one array

        for(int i=0;i<dp_client_value.size();i++){
            String[] name_arr = acc_type_detail.get(i).split("-");
            String[] value = dp_client_value.get(i).split("-");

//            array which is in csv format as row
            String[] res = {name_arr[0],name_arr[1],value[0],value[1],value[2]};
            result.add(res);
        }

//        if there is folio then we have to include it
        if(is_folio_exist){
            String current_string =arr.get(length);
            String seperate_str = "Folios";
            int n = seperate_str.length();
            int account_type_end = current_string.indexOf(seperate_str);
            String account_type = current_string.substring(0,account_type_end+n).replace("\r"," ").replace("\n"," ");
            String rest = current_string.substring(account_type_end+n,current_string.length()).replace("\r"," ").replace("\n"," ");
            rest = rest.trim();
            account_type = account_type.trim();
            String[] res_arr = rest.split(" ");
            String account_details = "";

            account_details+=res_arr[0]+res_arr[1];
            account_details=account_details.replace("\r"," ").replace("\n"," ");
            String value = res_arr[res_arr.length-1];


            String[] res = {account_type,account_details,"NA","NA",value};
            result.add(res);

        }


        return result;
    }


    public static ArrayList<String[]> get_meaningfull_para(String text){
        Pattern p_start = Pattern.compile("Account Type");
        Matcher m_start = p_start.matcher(text);
        Pattern p_end = Pattern.compile("Total");
        Matcher m_end = p_end.matcher(text);

        ArrayList<String[]> final_data = new ArrayList<>();


        int start = 0;
        int end = 0;
        String paragraph = "";
        while(m_start.find() && m_end.find()){
            int new_start = m_start.end();
            int new_end = m_end.start();
            if(new_end-new_start>=0){
                start = new_start;
                end= new_end;
                paragraph = text.substring(start,end);
                ArrayList<String[]> data =clean_paragraph(paragraph);
                for(String[] d: data){
                    if(!final_data.contains(d)){
                        final_data.add(d);
                    }
                }
            }
        }

        return final_data;

    }

    public ArrayList<String[]> get_whole_data(){
        return this.data;
    }

//    This method takes data and path and make csv for that path
    public static void make_csv(ArrayList<String[]> data , String path){
        String name = "Accounts"+".csv";
        // make file from giving path
        File file = new File(path+name);
        try{
            // export file
            FileWriter outputfile = new FileWriter(file);
            // convert normal file to csv
            CSVWriter writer = new CSVWriter(outputfile);
            // supply format for header
            String[] header_template = {"ACCOUNT TYPE", "ACCOUNT DETAIL", "DP ID" , "CLIENT ID" , "VALUE"};
            // write format for csv
            writer.writeNext(header_template);


            for(String[] value : data){
                writer.writeNext(value);
            }

            // close  file stream
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
//    this method generate data as array and make csv out of array
    public HashMap<String,ArrayList<String>> fetch_data(String text, String path){
    this.data = get_meaningfull_para(text);
    HashMap<String,ArrayList<String>> hash  = new HashMap<>();
    for(String[] arr: this.data){
        if((arr[2].equals("NA")) && (arr[3].equals("NA"))){}
        else{
            String key = "DP ID: "+arr[2]+" Client ID: "+arr[3];
            ArrayList<String> value = new ArrayList<>();
            value.add(arr[0]);
            value.add(arr[1]);
            hash.put(key,value);
        }
    }
    make_csv(this.data,path);
    return hash;
}

}

//This class is able to fetch data for physical folio from pdf
class Mutual_funds_folios{
//    this function return folio isin number
    public static String get_fund_folio_isin(String string){
        if(string.length()>=0){
            String isin = string.split("\n")[0];
            if(isin.isEmpty()){
                return "NA";
            }
            return isin;
        }
        return "NA";

    }

//    this function return isin description
    public static String get_fund_fulio_isin_description(String string){
        String[] temp = string.split("\n");
        String ans = "";
        if(temp.length>2){
            for(int i=2;i<temp.length-1;i++){
                String data = temp[i];
                data = data.replaceAll("\r", " ").replaceAll("\n"," ");
                String decimalPattern = "\\d(\\d|\\.|\\,|\\s)*";
                boolean match = Pattern.matches(decimalPattern, data);
                if(match){
                }
                else{
                    ans += data+" ";
                }
            }
            return ans;
        }
        return  "NA";
    }

//    this method return string of rest number which is in row
    public static String get_fund_folio_number(String string){
        String[] temp = string.split("\n");
        String ans = "";
        boolean flag = false;
        if(temp.length>2){
            for(int i=2;i<temp.length-1;i++){
                String data = temp[i];
                data = data.replaceAll("\r", " ").replaceAll("\n"," ");
                String decimalPattern = "\\d(\\d|\\.|\\,|\\s)*";
                boolean match = Pattern.matches(decimalPattern, data);
                if(match){
                    flag = true;
                    ans = data;
                    break;
                }
            }
        }
        if(flag==false){
            return temp[temp.length-1];
        }
        else{
            return ans;
        }
    }
//    this function fill missing data
    public static String[] fill_missing_data(String arr){
        int total_len = 8;
        Scanner sc = new Scanner(arr);
        while(sc.hasNext()){
            String curr_string = sc.nextLine();
            String[] temp = curr_string.split(" ");
            if(temp.length==8){
                return temp;
            }
            else{
                int difference = total_len - temp.length;
                for(int i=0;i<difference;i++){
                    curr_string+= " " + "NA";
                }
                temp = curr_string.split(" ");
                return temp;
            }

        }
        return new String[0];
    }

//    this method takes data and make csv out of thath
    public static void make_csv(ArrayList<String[]> arr,String path) throws IOException {
        String np = "Mutual_fund_folio"+".csv";
        File file = new File(path+np);
        FileWriter output = new FileWriter(file);
        CSVWriter csvWriter = new CSVWriter(output);
        String[] header = {"ACCOUNT TYPE","ACCOUNT DETAILS" ,"ISIN","ISIN NAME","FOLIO NUMBER","AVERAGE COST PER UNIT (Rs)","NO OF UNIT","INVESTMENT VALUE (Rs)","VALUE (Rs)"};
        csvWriter.writeNext(header);
        for(String[] value : arr){
            csvWriter.writeNext(value);
        }
        csvWriter.close();
    }

    public static int get_min_dist(String text,int start) {
        Pattern p = Pattern.compile("Notes");
        Matcher m = p.matcher(text);
        int min = Integer.MAX_VALUE;
        int end = 0;
        while (m.find()) {
            int dist = m.start() - start ;
            if (dist >= 0) {
                if (dist <= min) {
                    min = dist;
                    end = m.start();
                }
            }
        }
        return end;
    }


//    this method fetch all the data by pattern
    public static ArrayList<String[]> parse_fund_folios(String string){
        Pattern p = Pattern.compile("IN[F]\\d[A-Z0-9]{8}");
        Matcher m = p.matcher(string);
        ArrayList<Integer> index_list = new ArrayList<>();
        ArrayList<String[]> arr = new ArrayList<>();
        while(m.find()){
            index_list.add(m.start());
        }
        if(index_list.size()>0){

            for(int i=0;i<index_list.size()-1;i++){
                int start = index_list.get(i);
                int end = index_list.get(i+1);
                String[] temp_str = get_clean_fund_folis_row(string.substring(start,end));
                arr.add(temp_str);
            }
            String[] temp_str = get_clean_fund_folis_row(string.substring(index_list.get(index_list.size()-1),string.length()));
            arr.add(temp_str);
        }
        return arr;
    }

//    This function takes and paragraph and fetch the value out of that
    public static String[] get_clean_fund_folis_row(String string){
        String acc_type = "Mutual Fund Folios";
        String isin_val = get_fund_folio_isin(string);
        String isin_name = get_fund_fulio_isin_description(string);
        String folio_num_string = get_fund_folio_number(string);
        String[] number_arr = fill_missing_data(folio_num_string);

        String[] result = new String[0];
        String Folio_num = number_arr[0];
        String No_of_unit = number_arr[1];
        String avg_cost_per_unit = number_arr[2];
        String total_cost = number_arr[3];
        String curr_nav = number_arr[4];
        String curr_value = number_arr[5];
        int count = 0;
        String acc_det = count+" Folios";

//    this is array of the values which we want in csv
        result = new String[]{acc_type,acc_det,isin_val,isin_name,Folio_num,avg_cost_per_unit,No_of_unit,total_cost,curr_value};

        return result;
    }

//    this method fetch mutual fund data from whole data
    public static ArrayList<String[]> get_mutual_fund_folios(String text){
        String pat = "Mutual Fund Folios \\(F\\)";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        int start=  0;
        while(m.find()){
            start = m.start();
        }

        int end = get_min_dist(text,start);
        ArrayList<String[]> ans = parse_fund_folios(text.substring(start,end));
        return ans;
    }

//    this method return the folio count - 12 folios , 3 folios
    public static String get_folio_count(String text){
        Pattern p = Pattern.compile("[0-9]+ Folios");
        Matcher m = p.matcher(text);
        if(m.find()){
            return m.group();
        }
        return "NA";
    }
//    this method fetch data and make csv
    public static void fetch_data(String text, String path) throws IOException {
        ArrayList<String[]> folio_data = get_mutual_fund_folios(text);
        String folio_count = get_folio_count(text);
        for(String[] s : folio_data){
            s[1] = folio_count;
        }
        make_csv(folio_data,path);

    }
}

//This class is able to fetch  data for equity from pdf
class Equity_mutual{

// this method return nearest element distance from given pattern
    public static int get_min_distance(String text, String pat, int end ){
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        int min = Integer.MAX_VALUE;
        int start = 0;
        while(m.find()){
            int dist = end - m.start();
            if(dist>=0){
                if(dist<=min){
                    min = dist;
                    start = m.end();
                }
            }
        }
        return start;
    }

// this method return nearest element distance from given pattern
    public static int get_min_distance_s(String text, String pat, int start ){
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        int min = Integer.MAX_VALUE;
        int end = 0;
        while(m.find()){
            int dist = m.start() - start;
            if(dist>=0){
                if(dist<=min){
                    min = dist;
                    end = m.start();
                }
            }
        }
        return end;
    }

    //    this method build csv by given data
    public static void make_csv( ArrayList<String[]> arr,String path) throws IOException {
        File file = new File(path);
        FileWriter output = new FileWriter(file);
        CSVWriter csvWriter = new CSVWriter(output);
        String[] header = {"ACCOUNT TYPE","INTERMEDIARY","ISIN NUMBER","ISIN NAME","NUMBER OF SHARES","VALUE (Rs)"};
        csvWriter.writeNext(header);
        for(String[] value : arr){
            csvWriter.writeNext(value);
        }
        csvWriter.close();

    }

//    return isin number out of given string
    public static String get_isin_number(String string){
        Scanner sc= new Scanner(string);
        String ans = sc.nextLine();
        return ans;
    }
//    return stock symbol out of given string
    public static String get_stock_symbol(String string){
        Scanner sc= new Scanner(string);
        String ans = "NA";
        for(int i=0;i<2;i++){
            ans = sc.nextLine();
        }
        return ans;
    }
//    return company name out of given string
    public static String get_company_name(String string){
        String[] temp_arr = string.replace("\r","").split("\n");
        ArrayList<String> temp_part_arr = new ArrayList<>();
        for(String s: temp_arr){
            String[] parts = s.split(" ");
            for(String part : parts){
                temp_part_arr.add(part);
            }
        }
        String company_name  = "";
        if(temp_part_arr.size()>2){

            for(int i=2;i<temp_part_arr.size();i++){
                String cur_str = temp_part_arr.get(i);
                if(is_value_a_number(cur_str)){
                    break;
                }
                else{
                    company_name += cur_str + " ";
                }
            }


            return company_name;
        }
        return "NA";
    }

//  return value of amount by given string
    public static String[] get_values_amnt(String string){
        String[] temp_arr = string.replace("\r","").split("\n");
        ArrayList<String> temp_part_arr = new ArrayList<>();
        for(String s: temp_arr){
            String[] parts = s.split(" ");
            for(String part : parts){
                temp_part_arr.add(part);
            }
        }
        String numbers  = "";
        if(temp_part_arr.size()>2){
            boolean flag = false;
            for(int i=2;i<temp_part_arr.size();i++){
                String cur_str = temp_part_arr.get(i);
                if(is_value_a_number(cur_str)){
                    flag = true;
                    numbers+=cur_str+ " ";
                }
                else{
                    if(flag){
                        numbers+=cur_str+"\n";
                    }
                    else{
                        continue;
                    }
                }
            }
        }
        String[] imp_entry = numbers.split("\n");
        String result = "";
        String[] values = imp_entry[0].split(" ");
        for(int i=0;i<=3;i++){
            String value = values[i];
            if(is_value_a_number(value)){
                result+=value+" ";
            }
        }
        return result.split(" ");
    }
//  return isin number 2 by given string
    public static String get_isin_number_2(String string){
        String isin = string.split(" ")[0];
        return isin;
    }

    //    this method return account type by given paragraph
    public static String get_account_type(String text,HashMap<String,ArrayList<String>> hashmap){

//        DP ID: 12010900 Client ID: 07473694
        String pat = "DP ID: ((\\d){8}|IN(\\d){6}) Client ID: (\\d){8}";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        String key = "";
        while(m.find()){
            key  = m.group();
        }
        if(hashmap.containsKey(key)){
            ArrayList<String> value = hashmap.get(key);
            String ans = value.get(0).trim().replace("\r","");
            return ans;
        }
        return "NA";

    }
    //    this method return intermediary by given paragraph
    public static String get_intermediary(String text,HashMap<String,ArrayList<String>> hashmap){

//        DP ID: 12010900 Client ID: 07473694
        String pat = "DP ID: ((\\d){8}|IN(\\d){6}) Client ID: (\\d){8}";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        String key = "";
        while(m.find()){
            key = m.group();
        }
        if(hashmap.containsKey(key)){
            ArrayList<String> value = hashmap.get(key);
            return value.get(1).trim().replace("\r","");
        }

        return "NA";
    }

    //    this method return account type by given paragraph
    public static String get_account_type_eq_sh1(String text,HashMap<String,ArrayList<String>> hashmap){

//        DP ID: 12010900 Client ID: 07473694
        String pat = "DP ID: ((\\d){8}|IN(\\d){6}) Client ID: (\\d){8}";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        String key = "";
        if(m.find()){
            key  = m.group();
        }
        if(hashmap.containsKey(key)){
            ArrayList<String> value = hashmap.get(key);
            String ans = value.get(0).trim().replace("\r","");
            return ans;
        }
        return "NA";

    }
    //    this method return intermediary by given paragraph
    public static String get_intermediary_eq_sh1(String text,HashMap<String,ArrayList<String>> hashmap){

        String pat = "DP ID: ((\\d){8}|IN(\\d){6}) Client ID: (\\d){8}";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        String key = "";
        if(m.find()){
            key  = m.group();
        }
        if(hashmap.containsKey(key)){
            ArrayList<String> value = hashmap.get(key);
            String ans = value.get(1).trim().replace("\r","");
            return ans;
        }
        return "NA";
    }


    //    this method return boolean based on is number exists in array or not
    public static  boolean is_value_number_in_arr(String value){
        String decimalPattern = "[^a-zA-Z/-]*\\d(\\d|\\.|\\,)*[^a-zA-Z/-]*";
        String[] arr = value.split(" ");
        for(String ele : arr){
            boolean match = Pattern.matches(decimalPattern, ele);
            if(match){
                return true;
            }
        }
        return false;
    }
//    this method return boolean if value is number or not
    public static  boolean is_value_a_number(String value){
        String decimalPattern = "[^a-zA-Z/-]*\\d(\\d|\\.|\\,)*[^a-zA-Z/-]*";
        boolean match = Pattern.matches(decimalPattern, value);
        return match;
    }

//  this method return security name
    public static String get_security_2(String string){
        String[] data_list = string.split("\n");
        String security  = "";
        ArrayList<String> data = new ArrayList<>();
        for(String s : data_list){
            String[] nspace = s.split(" ");
            for(String n : nspace){
                n = n.replace("\r"," ");
                n=n.trim();
                data.add(n);
            }
        }
        for(int i=1;i<data.size();i++){
            String value = data.get(i);
            if(is_value_a_number(value)){
                break;
            }
            security+=value+" ";
        }
//        ArrayList<String> arr = new ArrayList<>();
//        String[] temp = data_list[0].split(" ");
//        for(int i=1;i<temp.length;i++){
//            String ele =  temp[i];
//            ele = ele.replaceAll("\r", " ").replaceAll("\n"," ");
//            security+=ele+" ";
//        }
//        int i=0;
//        for(i=1;i<data_list.length;i++){
//            String[] temp_arr = data_list[i].split(" ");
//            if(temp_arr.length<2){
//                if(is_value_number_in_arr(temp_arr[0])){
//                    break;
//                }
//            }
//            else{
//                for(String ele: temp_arr){
//                    ele = ele.replaceAll("\r", " ").replaceAll("\n"," ");
//                    security+=ele+" ";
//                }
//            }
//        }

        return security;
    }
//    this method return curr balance by parsing given string
    public static String get_curr_balance(String string){
        String[] data_list = string.split("\n");
        String curr_balance = "";
        for(int i=1;i<data_list.length;i++){
            String[] temp_arr = data_list[i].split(" ");
            if(temp_arr.length<2){
                if(is_value_number_in_arr(temp_arr[0])){
                    curr_balance = temp_arr[0];
                    break;
                }
            }
            else{
                continue;
            }
        }
//        ArrayList<String> data = new ArrayList<>();
//        for(String s : data_list){
//            String[] nspace = s.split(" ");
//            for(String n : nspace){
//                n = n.replace("\r"," ");
//                n=n.trim();
//                data.add(n);
//            }
//        }
//        for(int i=1;i<data.size();i++){
//            String value = data.get(i);
//            if(is_value_a_number(value)){
//                curr_balance = value;
//                break;
//            }
//        }
        return curr_balance;
    }
//    this method return  market price by parsing given string
    public static ArrayList<String> get_market_price(String string){
        String[] data_list = string.split("\n");
        String ans="";
        int i=0;
        ArrayList<String> res = new ArrayList<>();

        String curr_balance = "";

        for(i=1;i<data_list.length;i++){
            String[] temp_arr = data_list[i].split(" ");
            if(temp_arr.length<2){
                if(is_value_number_in_arr(temp_arr[0])){
                    curr_balance = temp_arr[0];
                    break;
                }
            }
            else{
                continue;
            }
        }
        res.add("NA");
        res.add("NA");

        for(int index = i;index<data_list.length;index++){
            String[] temp_arr = data_list[index].split(" ");
            if(temp_arr.length==2){
                res.set(0,temp_arr[0]);
                res.set(1,temp_arr[1]);
                break;
            }
        }

//        ArrayList<String> data = new ArrayList<>();
//        for(String s : data_list){
//            String[] nspace = s.split(" ");
//            for(String n : nspace){
//                n = n.replace("\r"," ");
//                n=n.trim();
//                data.add(n);
//            }
//        }
//        for(i=1;i<data.size();i++){
//            String value = data.get(i);
//            if(is_value_a_number(value)){
//                break;
//            }
//        }
//        for(int ind = i ;ind<data.size();ind++){
//            res.add(data.get(ind));
//
//        }
        return res;
    }

//    This method takes string and return arraylist of string of sections of data
    public static ArrayList<String> get_equity_sections(String text){
        Pattern p = Pattern.compile("IN[E][A-Z0-9]{9}");
        Matcher m = p.matcher(text);
        ArrayList<Integer> index  = new ArrayList<>();
        while(m.find()){
            index.add(m.start());
        }
        ArrayList<String> result= new ArrayList<>();
        for(int i=0;i<index.size()-1;i++){
            int start = index.get(i);
            int end = index.get(i+1);
            String curr = "";
            for(int j = start ;j<end; j++){
                curr+=text.charAt(j);
            }
            curr = curr.trim().replace("\r"," ");
            result.add(curr);
        }
        return result;
    }

//  this method parse the string and return well formed arraylist of string array
    public static ArrayList<String[]> get_parsed_equity_share_2(String text,HashMap<String,ArrayList<String>> hashmap){
        Pattern p = Pattern.compile("IN[E][A-Z0-9]{9}");
        Matcher m = p.matcher(text);
        ArrayList<String[]> arr = new ArrayList<>();



        while(m.find()){
            int start = m.start();
            String result = "";
            for(int i=start;i<text.length();i++){
                result+=text.charAt(i);
            }

            get_equity_sections(result);

            String isin = get_isin_number_2(result);
            String stock_symbol = get_security_2(result);
            String curr_balance = get_curr_balance(result);
            ArrayList<String> price_arr = get_market_price(result);
            String market_price = price_arr.get(0);
            String value_price =  price_arr.get(1);


            isin = isin.replace("\n"," ").replace("\r"," ");
            String[] faltu = isin.split(" ");
            isin = faltu[0];
            stock_symbol = stock_symbol.replace("\n"," ").replace("\r"," ");

            curr_balance = curr_balance.replace("\n"," ").replace("\r"," ");
            market_price = market_price.replace("\n"," ").replace("\r"," ");
            value_price = value_price.replace("\n"," ").replace("\r"," ");



            String[] values = {isin,"NA","NA",stock_symbol,curr_balance,market_price,value_price};
            arr.add(values);
        }

        return arr;
    }

    public static ArrayList<String[]> get_parsed_equity_share_2_demo(String text,HashMap<String,ArrayList<String>> hashmap){
        Pattern p = Pattern.compile("IN[E][A-Z0-9]{9}");
        Matcher m = p.matcher(text);
        ArrayList<String[]> arr = new ArrayList<>();



        while(m.find()){
            int start = m.start();
            String result = "";
            for(int i=start;i<text.length();i++){
                result+=text.charAt(i);
            }

            ArrayList<String> sections = get_equity_sections(result);
            for(String section: sections){
                String isin = get_isin_number_2(section);
                String stock_symbol = get_security_2(section);
                String curr_balance = get_curr_balance(section);
                ArrayList<String> price_arr = get_market_price(section);
                String market_price = price_arr.get(0);
                String value_price =  price_arr.get(1);


                isin = isin.replace("\n"," ").replace("\r"," ");
                String[] faltu = isin.split(" ");
                isin = faltu[0];
                stock_symbol = stock_symbol.replace("\n"," ").replace("\r"," ");

                curr_balance = curr_balance.replace("\n"," ").replace("\r"," ");
                market_price = market_price.replace("\n"," ").replace("\r"," ");
                value_price = value_price.replace("\n"," ").replace("\r"," ");



                String[] values = {isin,"NA","NA",stock_symbol,curr_balance,market_price,value_price};
                arr.add(values);
            }

        }

        return arr;
    }
//  this method parse the string and return well formed arraylist of string array
    public static  ArrayList<String[]>  get_parsed_equity_share(String string){
        Pattern p = Pattern.compile("IN[E][A-Z0-9]{9}");
        Matcher m = p.matcher(string);
        ArrayList<String[]> arr = new ArrayList<>();
        while(m.find()){
            int start = m.start();
            String result = "";
            for(int i=start;i<string.length();i++){
                result+=string.charAt(i);
            }
            String isin = get_isin_number(result);
            String stock_symbol = get_stock_symbol(result);
            String get_compny_name = get_company_name(result);
            String get_values[] = get_values_amnt(result);


            String[] values = {isin,"","",stock_symbol,get_compny_name,get_values[0],get_values[1],get_values[2],get_values[3]};
            arr.add(values);
        }
        return arr;
    }

//    this method fetch section of equity and after cleaning it returns arraylist of string array
    public static  ArrayList<String[]>  get_equity_share_1(String text,HashMap<String,ArrayList<String>> hashmap){
        Pattern p = Pattern.compile("Equities \\(E\\)");
        Matcher m = p.matcher(text);
        boolean b = m.find();

        Pattern p_end = Pattern.compile("Sub Total");
        Matcher m_end = p_end.matcher(text);
        boolean b_end = m_end.find();

        String account_type="NA";
        String intermediary="NA";
        ArrayList<String[]> arr = new ArrayList<>();

        if(b){
            int end = m.start();
        }
        if(m.find() && b_end){
            String curr_string = "";
            for(int i=m.end();i<m_end.start();i++){
                curr_string+=text.charAt(i);
            }
            arr = get_parsed_equity_share(curr_string);

            String string  = text.substring(0,m.start());
            account_type = get_account_type_eq_sh1(text,hashmap);
            intermediary = get_intermediary_eq_sh1(text,hashmap);

        }
        for(String[] s: arr){
            s[1] = account_type.replace("\r"," ");
            s[2] = intermediary.replace("\r"," ");
        }
        return arr;
    }
//    this method fetch section of equity and after cleaning it returns arraylist of string array
    public static ArrayList<String[]> get_equity_share_2(String text,HashMap<String,ArrayList<String>> hashmap){
        Pattern p = Pattern.compile("Equities \\(E\\)");
        Matcher m = p.matcher(text);
        boolean b = m.find();

        Pattern p_end = Pattern.compile("Sub Total");
        Matcher m_end = p_end.matcher(text);
        boolean b_end = m_end.find();

        int start = 0;
        int end = 0;

        ArrayList<String[]> result = new ArrayList<>();


        while(m.find()){
            int new_start = m.start();
            int new_end = get_min_distance_s(text,"Sub Total",new_start);

            String string  = text.substring(start,new_start);
            String account_type = get_account_type(string,hashmap);
            String intermediary = get_intermediary(string,hashmap);


            start = new_start;
            end = new_end;

            ArrayList<String[]> arr = new ArrayList<>();
//            chnage
            String curr_ans = text.substring(start,end);
            arr = get_parsed_equity_share_2(curr_ans,hashmap);



            for(String[] s_ele : arr){
                s_ele[1] = account_type;
                s_ele[2] = intermediary;
                if(!result.contains(s_ele)){
                    result.add(s_ele);
                }
            }
        }

        return  result;

    }


//    this method return arraylist of string arr by format of cas nsdl parser
    public static ArrayList<String[]> get_final_managed_data_as_cas(ArrayList<String[]> result){
        ArrayList<String[]> final_result= new ArrayList<>();
        for(String[] arr : result){
            String  account_type = arr[1];
            String intermediary = arr[2];
            String isin = arr[0];
            String isin_name = arr[4];
            String no_of_share = arr[6];
            String value = arr[8];

            String curr_result[] = new String[6];
            curr_result[0] = account_type;
            curr_result[1] = intermediary;
            curr_result[2] = isin;
            curr_result[3] = isin_name;
            curr_result[4] = no_of_share;
            curr_result[5] = value;
            final_result.add(curr_result);
        }

        return final_result;
    }
//    this method takes two array and merge it
    public static ArrayList<String[]> merge_eq_one_two(ArrayList<String[]> ar1, ArrayList<String[]> ar2){
        ArrayList<String[]> result = new ArrayList<>();



        for(String[] s_arr : ar1){
            String isin = s_arr[0];
            String account_type = s_arr[1];
            String intermidiary = s_arr[2];
            String stock_sys = s_arr[3];
            String company_name = s_arr[4];
            String face_value = s_arr[5];
            String num = s_arr[6];
            String market_price = s_arr[7];
            String value = s_arr[8];
            String[] curr_res= {isin,account_type,intermidiary,stock_sys,company_name,face_value,num,market_price,value};
            result.add(curr_res);
        }
        for(String[] s_arr : ar2){
            String isin = s_arr[0];
            String account_type= s_arr[1];
            String intermediary = s_arr[2];
            String stock_sys = "NA";
            String company_name = s_arr[3];
            String face_value = "NA";
            String num =  s_arr[4];
            String market_price = s_arr[5];
            String value = s_arr[6];
            String[] curr_res= {isin,account_type,intermediary,stock_sys,company_name,face_value,num,market_price,value};
            result.add(curr_res);
        }
        return result;
    }

//    This method takes the arraylist of string array and return unique filled values of elements
    public static ArrayList<String[]> get_unique_arr_list(ArrayList<String[]> arr){



        HashMap<String,String[]> hashmap = new HashMap<>();
        ArrayList<String[]> result = new ArrayList<>();

        for(String[] s : arr){
            String key = s[0];
            String[] value = new String[s.length-1];
            for(int i=0;i<s.length-1;i++){
                String val = s[i+1];
                value[i] = val;
            }
            if(!hashmap.containsKey(key)){
                hashmap.put(key,value);
            }
            else{
                String[] old_value = hashmap.get(key);
                String[] new_val = value;
                String[] ans = new String[old_value.length];
                for(int i =0;i<old_value.length;i++){
                    String old = old_value[i];
                    String newv = new_val[i];
                    if(old.equals("NA")){
                        ans[i]  = newv;
                    }
                    else{
                        ans[i] = old;
                    }
                }


                hashmap.put(key,ans);
            }
        }

        for(String key : hashmap.keySet()){
            String zeros = key;
            String[] rest = hashmap.get(key);
            String[] curr_res = new String[rest.length+1];
            curr_res[0] = zeros;
            for(int i=0;i<rest.length;i++){
                curr_res[i+1] =  rest[i];
            }
            result.add(curr_res);
        }

        return result;
    }


//    this method return arraylist of string array by cleaning arrays of both sections
    public static ArrayList<String[]> get_equity(String text,HashMap<String,ArrayList<String>> hashmap) throws IOException {
//        take equity share 1
        ArrayList<String[]> arr1 = get_equity_share_1(text,hashmap);
//        take rest equity shares
        ArrayList<String[]> arr2 = get_equity_share_2(text,hashmap);
//      merge both equity
        ArrayList<String[]> result =  merge_eq_one_two(arr1,arr2);
//        remove duplicates
        result = get_unique_arr_list(result);
//        arrange the items as cas reader format
        ArrayList<String[]> final_content = get_final_managed_data_as_cas(result);



        return final_content;
    }


//  this method is able to get parsed array and make csv out of that
    public static ArrayList<String[]> fetch_data(String text,String path,HashMap<String,ArrayList<String>> hashmap) throws IOException {
        ArrayList<String[]> result = get_equity(text,hashmap);
//        make_csv(result,path+"Equitys"+".csv");
        return result;
    }
}

// This class is able to fetch data for mutual funds from pdf
class Mutual_funds{
    //    This method takes stringarray of arraylist and path and make csv outof that
    public static  void make_csv(ArrayList<String[]> data , String path) throws IOException {
        String name = "Mutual_funds" + ".csv";
        File file = new File(path+name);
        FileWriter output = new FileWriter(file);
        CSVWriter csvWriter = new CSVWriter(output);
        String[] header = {"ACCOUNT TYPE","INTERMEDIARY","ISIN", "ISIN NAME","NUMBER OF UNITS","VALUE (Rs)"};
        csvWriter.writeNext(header);
        for(String[] value : data){
            csvWriter.writeNext(value);
        }
        csvWriter.close();
    }
    //    This method returns nearest pattern position / index
    public static int get_min_dist(String text,String pat,int start) {
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        int min = Integer.MAX_VALUE;
        int end = 0;
        while (m.find()) {
            int dist = m.start() - start ;
            if (dist >= 0) {
                if (dist <= min) {
                    min = dist;
                    end = m.start();
                }
            }
        }
        return end;
    }

    //    This method returns boolean output based on is This paragraph required for csv or not
    public static boolean is_portion_required(String para){
        Pattern p_not_required = Pattern.compile("Money Market Instruments \\(I\\) ");
        Matcher m_not_required = p_not_required.matcher(para);
        boolean is_found_not_required = m_not_required.find();
        return is_found_not_required;
    }
    //    This method returns boolean output based on given string is it value or not
    public static  boolean is_value_number(String value){
        String decimalPattern = "[^a-zA-Z/-]*\\d(\\d|\\.|\\,)*[^a-zA-Z/-]*";
        boolean match = Pattern.matches(decimalPattern, value);
        return match;
    }


    //    This method returns isin number from arraylist
    public static String get_isin(ArrayList<String> arrlist){
        String isin  = arrlist.get(0);
        return isin;
    }
    //    This method returns isin name from arraylist
    public static String get_isin_name(ArrayList<String> arrlist){
        String isin_name = "";
        for(int i=1;i<arrlist.size();i++){
            String curr_str = arrlist.get(i);
            if(is_value_number(curr_str)){
                break;
            }
            else{
                isin_name+=curr_str+" ";
            }
        }
        return isin_name;
    }
    //    This method returns number of unit from arraylist
    public static String get_num_of_unit(ArrayList<String> arrlist){
        String num_of_unit = "";
        for(int i=0;i< arrlist.size();i++){
            String curr_str= arrlist.get(i);
            if(is_value_number(curr_str)){
                num_of_unit = curr_str;
                break;
            }
        }
        return num_of_unit;
    }
    //    This method returns value from arraylist
    public static String get_value(ArrayList<String> arrlist){
        String value = "";
        for(int i=0;i< arrlist.size();i++){
            String curr_str= arrlist.get(i);
            if(is_value_number(curr_str)){
                value = curr_str;
            }
        }
        return value;
    }


    //    this method return account type by given paragraph
    public static String get_account_type(String text,HashMap<String,ArrayList<String>> hashmap){

//        DP ID: 12010900 Client ID: 07473694
        String pat = "DP ID: (\\d){8} Client ID: (\\d){8}";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        String key = "";
        if(m.find()){
            key  = m.group();
        }
        if(hashmap.containsKey(key)){
            ArrayList<String> value = hashmap.get(key);
            String ans = value.get(0).trim().replace("\r","");
            return ans;
        }
        return "NA";

    }
    //    this method return intermediary by given paragraph
    public static String get_intermediary(String text,HashMap<String,ArrayList<String>> hashmap){

//        DP ID: 12010900 Client ID: 07473694
        String pat = "DP ID: (\\d){8} Client ID: (\\d){8}";
        Pattern p = Pattern.compile(pat);
        Matcher m = p.matcher(text);
        String key = "";
        if(m.find()){
            key = m.group();
        }
        if(hashmap.containsKey(key)){
            ArrayList<String> value = hashmap.get(key);
            return value.get(1).trim().replace("\r","");
        }

        return "NA";
    }

    //    This method takes string and out of that merged section it parsed and return Stringarray arraylist
    public static ArrayList<String[]> get_parsed_mutual_funds(String string){
//        "IN[F][A-Z0-9]{9}"
        Pattern p = Pattern.compile("IN[F]([0-9]{3})([A-Z0-9]{6})");
        Matcher m = p.matcher(string);
        ArrayList<ArrayList<String>> sections_array  = new ArrayList<>();
        ArrayList<Integer> index_arr = new ArrayList<>();
        ArrayList<String[]> result_arrlist = new ArrayList<>();
        while(m.find()){
            int start = m.start();
            index_arr.add(start);
        }
        for(int i=0;i<index_arr.size();i++){
            if(i == index_arr.size()-1){
                int start = index_arr.get(index_arr.size()-1);
                int end = string.length();
                String result= "";
                for(int j = start;j<end;j++){
                    result+=string.charAt(j);
                }
                ArrayList<String> result_arr = new ArrayList<>();
                result = result.replace("\r"," ");
                String[] by_new_line = result.split("\n");
                for(String str: by_new_line){
                    String[] by_space = str.split(" ");
                    for(String s : by_space){
                        result_arr.add(s);
                    }
                }
                sections_array.add(result_arr);
            }
            else{
                int start = index_arr.get(i);
                int end = index_arr.get(i+1);
                String result= "";
                for(int j = start;j<end;j++){
                    result+=string.charAt(j);
                }
                ArrayList<String> result_arr = new ArrayList<>();
                result = result.replace("\r"," ");
                String[] by_new_line = result.split("\n");
                for(String str: by_new_line){
                    String[] by_space = str.split(" ");
                    for(String s : by_space){
                        result_arr.add(s);
                    }
                }
                sections_array.add(result_arr);
            }
        }

        for(ArrayList<String> section : sections_array){
            String isin = get_isin(section);
            String isin_name = get_isin_name(section);
            String num_of_un = get_num_of_unit(section);
            String value  = get_value(section);
            String[] arr = {"NA","NA",isin,isin_name,num_of_un,value};
            result_arrlist.add(arr);
        }
        return result_arrlist;
    }

    //    This method takes text and make sections of various mutual funds tables
    public static ArrayList<String[]> get_section_for_mutual_funds(String text, HashMap<String,ArrayList<String>>  hashmap){
        Pattern p = Pattern.compile("Mutual Funds \\(M\\)");
        Matcher m = p.matcher(text);
        String data = "";
        String curr_str = "";
//        first pattern which find is not included so avoid that
        while(m.find()){
            int start = m.start();
            int end = get_min_dist(text,"Sub Total",start);
            String portion = text.substring(start,end);
            boolean is_required = is_portion_required(portion);
            if(is_required) {
            }
            else{
                curr_str = text.substring(0,m.start());
                data+=portion;
            }
        }

        String acc_type= get_account_type(curr_str,hashmap);
        String inter = get_intermediary(curr_str,hashmap);

        ArrayList<String[]> arr = get_parsed_mutual_funds(data);


        for(String[] s : arr){
            s[0] = acc_type;
            s[1] = inter;
        }
        return arr;
    }

    //    This is main method in which we give text and path and it makes it in structure format and convert it into csv
    public static ArrayList<String[]> fetch_data(String text, String path , HashMap<String,ArrayList<String>>  hashmap ) throws IOException {
        ArrayList<String[]> data = get_section_for_mutual_funds(text,hashmap);
//        make_csv(data,path);
        return data;
    }
}

public class Main {
// this method takes array of equity and mutual funds and path and make csv  after merging both
    public static void merge_and_make_csv(ArrayList<String[]> eq , ArrayList<String[]> mutu, String path) throws IOException {
        String name = "Assets" + ".csv";
        File file = new File(path+name);
        FileWriter output = new FileWriter(file);
        CSVWriter csvWriter = new CSVWriter(output);
        String[] header = {"ACCOUNT TYPE","INTERMEDIARY","ISIN", "ISIN NAME","NUMBER OF UNITS","VALUE (Rs)"};
        csvWriter.writeNext(header);
        for(String[] value : eq){
            csvWriter.writeNext(value);
        }

        for(String[] value : mutu){
            csvWriter.writeNext(value);
        }
        csvWriter.close();
    }
//    this method set acc to  format
    public static ArrayList<String[]> set_as_format(ArrayList<String[]> format,ArrayList<String[]> equity){
        ArrayList<String[]> result = new ArrayList<>();
        for(String[] format_list: format){
            String form = format_list[0];
            for(String[] equity_list : equity){
                String equi = equity_list[0];
                if(form.equals(equi)){
                    result.add(equity_list);
                }
            }
        }
        return result;
    }
    public static void main(String args[]) throws IOException {
        //Loading an existing document
        String[] password = {"ALCPG3121D","AAGHJ3431E","AABPG7948J"};
        for(int i=1;i<=3;i++){

//            File file = new File("D:\\INTERNSHIP_MYDHAN\\Pdf_parsor_with_to_csv\\src\\main\\resources\\sample_"+i+".pdf");

//           opne file with proper path
            File file = new File("C:\\Users\\HP\\OneDrive\\Desktop\\Mydhan\\nsdl_parser\\Pdf_parsor_with_to_csv\\src\\main\\resources\\password_sample_"+i+".pdf");
            PDDocument document = PDDocument.load(file,password[i-1]);

//            passwords of the file
//            1 - ALCPG3121D
//            2 - AAGHJ3431E
//            3 - AABPG7948J


            //Instantiate PDFTextStripper class
            PDFTextStripper pdfStripper = new PDFTextStripper();
            //Retrieving text from PDF document
            String text = pdfStripper.getText(document);

//            curr date
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/mm/yyyy");
            LocalDateTime now = LocalDateTime.now();
            String date_formate = dtf.format(now).replace("/","-");

//            goal path where the csv geberated
            String path = "C:\\Users\\HP\\OneDrive\\Desktop\\"+date_formate+i+"\\";


            // removing all security from PDF file
            document.setAllSecurityToBeRemoved(true);

            // Save the PDF file
            document.save(file);

             System.out.println("Decryption Done...");

             File f = new File(path);
// make folder
             f.mkdir();


            Credentials credentials = new Credentials();
            credentials.fetch_user_info(text,path);

            Holdings holdings = new Holdings();
            holdings.fetch_data(text,path);

            Composition composition = new Composition();
            composition.fetch_data(text,path);

            LifeInsurance lifeInsurance = new LifeInsurance();
            lifeInsurance.fetch_data(text,path);

            Accounts accounts = new Accounts();
            HashMap<String,ArrayList<String>>  hashmap =  accounts.fetch_data(text,path);
            ArrayList<String[]> acc_data = accounts.get_whole_data();

            Equity_mutual equity_mutual = new Equity_mutual();
            ArrayList<String[]> equity = equity_mutual.fetch_data(text,path,hashmap);

            Mutual_funds mutual_funds = new Mutual_funds();
            ArrayList<String[]> mutual_f = mutual_funds.fetch_data(text,path,hashmap);

//            ArrayList<String[]> equity_new = set_as_format(acc_data,equity);
//            ArrayList<String[]> mutual_new = set_as_format(acc_data,mutual_f);
            merge_and_make_csv(equity,mutual_f,path);

            Mutual_funds_folios mutual_fund_folios = new Mutual_funds_folios();
            mutual_fund_folios.fetch_data(text,path);


            document.close();
        }



    }}