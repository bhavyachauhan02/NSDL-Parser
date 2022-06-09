import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Temp {
    public static void main(String[] args) throws IOException, ParseException {

        String path = "C:\\Users\\HP\\OneDrive\\Desktop\\Mydhan\\nsdl_parser\\Pdf_parsor_with_to_csv\\src\\main\\resources\\response.json";
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(new FileReader(path));
        JSONObject jsonobj =  (JSONObject) obj;


        JSONArray arr = (JSONArray) jsonobj.get("value");
        ArrayList<String[]> data = new ArrayList<>();
        for(int i=0;i<arr.size();i++){
            String id = ""+i;
            JSONObject curr_jsonobj = (JSONObject) arr.get(i);
            String rcv_date = (String) curr_jsonobj.get("receivedDateTime");
            String content = (String) curr_jsonobj.get("bodyPreview");
            String subject = (String) curr_jsonobj.get("subject");
            JSONObject from_obj = (JSONObject) curr_jsonobj.get("from");
            JSONObject email_add = (JSONObject) from_obj.get("emailAddress");
            String sender_name = (String) email_add.get("name");
            String sender_mail = (String) email_add.get("address");

            sender_name = sender_name.replace("\r"," ").replace("\n"," ");
            subject = subject.replace("\r"," ").replace("\n"," ");
            rcv_date = rcv_date.replace("\r"," ").replace("\n"," ");
            sender_mail = sender_mail.replace("\r"," ").replace("\n"," ");
            content = content.replace("\r"," ").replace("\n"," ");
            String[] string_arr = new String[7];
            string_arr[0] = id;
            string_arr[1] = sender_name;
            string_arr[2] = "amount";
            string_arr[3] = subject;
            string_arr[4] = rcv_date;
            string_arr[5] = sender_mail;
            string_arr[6] = content;
            data.add(string_arr);
        }

        for(String[] s: data){
            System.out.println(s[5]);
        }
    }
}
