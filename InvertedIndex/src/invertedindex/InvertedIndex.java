
package invertedindex;


import java.io.*;
import java.util.*;

//=====================================================================
class DictEntry {

    public int doc_freq = 0;
    public int term_freq = 0;
    public HashSet<Integer> postingList;

    DictEntry() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index {

    //--------------------------------------------
    Map<Integer, String> sources;  
    HashMap<String, DictEntry> index; 
    //--------------------------------------------

    Index() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry>();
    }

    //---------------------------------------------
    public void printPostingList(HashSet<Integer> hset) {
        Iterator<Integer> it2 = hset.iterator();
        while (it2.hasNext()) {
            System.out.print(it2.next() + ", ");
        }
        System.out.println("");
    }
     public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        System.out.println("------------------------------------------------------");
        System.out.println("*****    Number of terms = " + index.size());
        System.out.println("------------------------------------------------------");

    }

    //-----------------------------------------------
     public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
         printDictionary();
    }

    
    
    
    HashSet<Integer> and(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();
        ArrayList<Integer> al1= new ArrayList<>(pL1);
        Collections.sort(al1);
        ArrayList<Integer> al2= new ArrayList<>(pL2);
        Collections.sort(al2);
        Iterator<Integer> itP1 = al1.iterator();
        Iterator<Integer> itP2 = al2.iterator();
        int docId1 = 0, docId2 = 0,count=0,option=0;
       
        while (itP1.hasNext() || itP2.hasNext()) {
            
            if (count==0)
            {
                count++;
                if (itP1.hasNext()) 
                    docId1 = itP1.next(); 
                if (itP2.hasNext()) 
                    docId2 = itP2.next();
            }
            
            if (option==1)
            {
                if (itP1.hasNext() && itP2.hasNext()) 
                {
                     docId1 = itP1.next();
                     docId2 = itP2.next();
                }
                else 
                    break;
            }
            else if (option==2)
            {
             
                if (itP1.hasNext()) 
                    docId1 = itP1.next();
                 else return answer;
            }
            else if (option==3)
            {
                if (itP2.hasNext()) 
                    docId2 = itP2.next();
                else return answer;
            }
            
            
            if (docId1 == docId2) {
                answer.add(docId1);
                option=1;
                
               
            } 
            else if (docId1 < docId2) {               
                option=2;
                
            } else {
                   option=3;
                
                
            }
          
        }
        if (docId1 == docId2) {
            answer.add(docId1);
        }
        return answer;
    }
    //-----------------------------------------------------------------------   
    
     HashSet<Integer> or(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();
        ArrayList<Integer> al1= new ArrayList<>(pL1);
        Collections.sort(al1);
        ArrayList<Integer> al2= new ArrayList<>(pL2);
        Collections.sort(al2);
        
        Iterator<Integer> itP1 = al1.iterator();
        Iterator<Integer> itP2 = al2.iterator();
        int docId1 = 0, docId2 = 0;
        boolean finished1=false,finished2=false;
       
        if (itP1.hasNext()) 
            docId1 = itP1.next(); 
        if (itP2.hasNext()) 
            docId2 = itP2.next();
        while (finished1==false || finished2==false) {
           
           
            if (docId1 == docId2) {
                
                if (itP1.hasNext() && itP2.hasNext()) {
                    answer.add(docId1);
                    docId1 = itP1.next();
                    docId2 = itP2.next();
                }
                else if (itP1.hasNext())
                {
                    finished2=true;
                    docId1 = itP1.next();
                }
                else if (itP2.hasNext())
                {
                   
                    finished1=true;
                    docId2 = itP2.next();
                }
                else
                {
                    answer.add(docId1);
                     finished1=true;
                     finished2=true;
                }
            } 
            else if (docId1 < docId2) { 
                
               
                if (itP1.hasNext()) 
                {
                     answer.add(docId1);
                     docId1 = itP1.next();
                }
                else if (itP2.hasNext())
                {
                    finished1=true;
                    answer.add(docId1);
                    answer.add(docId2);
                    docId2 = itP2.next();
                }
                else
                {
                    finished1=true;
                    finished2=true;
                    answer.add(docId1);
                    answer.add(docId2);
                    
                }
                
                
            } else {
                if (itP2.hasNext()) 
                {
                     answer.add(docId2);
                     docId2 = itP2.next();
                }
                else if (itP1.hasNext())
                {
                    finished2=true;
                    answer.add(docId1);
                    answer.add(docId2);
                    docId1 = itP1.next();
                }
                else
                {
                    
                    finished1=true;
                    finished2=true;
                    answer.add(docId1);
                    answer.add(docId2);
                    
                }
                
            }
          
        }
      
        return answer;
    }
    //-----------------------------------------------------------------------   
     HashSet<Integer> not(HashSet<Integer> pL1) {
        HashSet<Integer> answer = new HashSet<Integer>();
        ArrayList<Integer> al1= new ArrayList<>(pL1);
        Collections.sort(al1);
        
        Iterator<Integer> itP1 = al1.iterator();
        Iterator<Integer> itP2 = sources.keySet().iterator();
        int docId1 = 0, docId2 = 0;
        if (itP1.hasNext()) 
                docId1 = itP1.next(); 
        
        while ( itP2.hasNext()) {
            if (itP2.hasNext()) 
                docId2 = itP2.next();
           
            if (docId1 == docId2) {
                if (itP1.hasNext()) 
                {
                     docId1 = itP1.next();
                } 
            } 
            else if (docId2 < docId1) { 
                answer.add(docId2);  
                
            } else {
                if (itP1.hasNext()) 
                {
                    docId1 = itP1.next();
                }
                else 
                {
                    answer.add(docId2);
                }
            }
        }
       
        return answer;
    }
    //-----------------------------------------------------------------------

   
    //-----------------------------------------------------------------------         
    public HashSet<Integer> find(String phrase) { // any mumber of terms optimized search 
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        HashSet<Integer> res = new HashSet<Integer>();
        
       
       int i=0;
           while (true)
            {
                if (i==len)
                    break;
                if (res.size()==0)
                {
                       
                    if (words[i].toLowerCase().equals("and"))
                    {
                        
                        res = index.get(words[i-1].toLowerCase()).postingList;
                      
                        
                        if (words[i+1].toLowerCase().equals("not"))
                        {
                            
                            res = and(res, not(index.get(words[i+2].toLowerCase()).postingList));
                        }
                        else
                        {
                           res = and(res, index.get(words[i+1].toLowerCase()).postingList); 
                          
                        }
                        
                    }
                    else if (words[i].toLowerCase().equals("or"))
                    {
                        
                        res = index.get(words[i-1].toLowerCase()).postingList;
                        if (words[i+1].toLowerCase().equals("not"))
                        {
                           
                            res = or(res, not(index.get(words[i+2].toLowerCase()).postingList));
                        }
                        else
                        {
                           
                           res = or(res, index.get(words[i+1].toLowerCase()).postingList);   
                        }
                        
                    }
                    
                    else if (words[i].toLowerCase().equals("not"))
                    {
                        
                        res = not(index.get(words[i+1].toLowerCase()).postingList);
                    }
                }
                else
                {
                    if (words[i].toLowerCase().equals("and"))
                    {
                        if (words[i+1].toLowerCase().equals("not"))
                        {
                            
                            res = and(res, not(index.get(words[i+2].toLowerCase()).postingList));
                        }
                        else
                        {
                           
                           res = and(res, index.get(words[i+1].toLowerCase()).postingList);   
                        }
                    }
                    else if (words[i].toLowerCase().equals("or"))
                    {
                        
                        if (words[i+1].toLowerCase().equals("not"))
                        {
                            
                            res = or(res, not(index.get(words[i+2].toLowerCase()).postingList));
                        }
                        else
                        {
                           
                           res = or(res, index.get(words[i+1].toLowerCase()).postingList);   
                        }
                        
                    }
                    
                }   
                 i++;
            } 
           
        
        for (int num : res) {
           // System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }
        return res;
    }
          

  
}

public class InvertedIndex {

    public static void main(String args[]) throws IOException {
        Index index = new Index();
        
        index.buildIndex(new String[]{         
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\100.txt", 
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\101.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\102.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\103.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\104.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\105.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\106.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\107.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\108.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\109.txt" ,
             "D:\\4th year\\Second term\\ir\\assignment\\docs\\300.txt" ,
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\302.txt" ,
             "D:\\4th year\\Second term\\ir\\assignment\\docs\\500.txt", 
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\501.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\502.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\503.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\504.txt",   
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\505.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\506.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\507.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\508.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\509.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\510.txt",   
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\511.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\512.txt",
             "D:\\4th year\\Second term\\ir\\assignment\\docs\\513.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\514.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\515.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\516.txt",   
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\517.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\518.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\519.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\520.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\521.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\522.txt",   
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\523.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\524.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\525.txt",
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\526.txt",   
            "D:\\4th year\\Second term\\ir\\assignment\\docs\\527.txt",
           
        });  
        
        System.out.println("dynamism"+ index.index.get("dynamism").postingList);
        System.out.println("independently"+ index.index.get("independently").postingList);
        System.out.println("response"+ index.index.get("response").postingList);
        System.out.println("carries"+ index.index.get("carries").postingList);
        System.out.println("shortened"+ index.index.get("shortened").postingList);
        System.out.println("coordinate"+ index.index.get("coordinate").postingList);
        System.out.println("deficiencies"+ index.index.get("deficiencies").postingList);
        System.out.println("delegation"+ index.index.get("delegation").postingList);
        System.out.println("cat"+ index.index.get("cat").postingList);
        System.out.println("alarm"+ index.index.get("alarm").postingList);
        System.out.println("efficacy"+ index.index.get("efficacy").postingList);
        System.out.println("cyclically"+ index.index.get("cyclically").postingList);
        System.out.println("consists"+ index.index.get("consists").postingList);
        System.out.println("--------------------------------------------------------");        
        
        System.out.println("\n"+"Query1: not dynamism");
        System.out.println(index.find("not dynamism"));
        
        System.out.println("Query2: response and carries");
        System.out.println(index.find("response and carries"));

        System.out.println("Query3: dynamism or independently");
        System.out.println(index.find("dynamism or independently"));
        
        System.out.println("Query4: shortened and coordinate and deficiencies");
        System.out.println(index.find("shortened and coordinate and deficiencies"));
        
        System.out.println("Query5: shortened or coordinate or deficiencies");
        System.out.println(index.find("shortened or coordinate or deficiencies"));
        
        System.out.println("Query6: independently and not dynamism or delegation");
        System.out.println(index.find("independently and not dynamism or delegation"));
        
        System.out.println("Query7: dynamism and not delegation");
        System.out.println(index.find("dynamism and not delegation"));
        
        System.out.println("Query8: not cat or alarm and efficacy");
        System.out.println(index.find("not cat or alarm and efficacy"));
        
        System.out.println("Query9: cyclically or not consists");
        System.out.println(index.find("cyclically or not consists"));

    
     
    }
}
