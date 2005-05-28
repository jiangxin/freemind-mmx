#include <process.h> 
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
char *surround_by_quote(char *in_string) {
   char *result = (char *) malloc((strlen(in_string) + 2 + 1 ) * sizeof(char));
   result[0] = '"';
   strcpy(result + 1 , in_string);
   result[strlen(in_string)+1] = '"';
   result[strlen(in_string)+2] = 0;
   return result; }

int main(int argc, char *argv[])  { 
    // argv[0] - caller name, argv[argc -1] == last argument,
   char *application_name = "lib\\freemind.jar";
   
   char **arguments = (char **) malloc(( argc - 1 + 3 + 1) * sizeof(char*));

   // Pick the path from argv[0]. This is for the case, that the launcher is not
   // started from the folder, in which it resides.
   char *full_name = application_name;
   if (char *pos = strrchr(argv[0],'\\')) {
      int prefix_length = pos - argv[0] + 1;
      full_name = (char *) malloc((prefix_length + strlen(application_name) + 1 ) * sizeof(char));
      memcpy(full_name,argv[0],prefix_length);
      strcpy(full_name + prefix_length, application_name); }
      
   arguments[0] = "javaw.exe";
   arguments[1] = "-jar";
   arguments[2] = surround_by_quote(full_name);
   
   for (int i=1; i<argc; ++i) {
      arguments[2+i] = surround_by_quote(argv[i]); }

   arguments[2+argc] = (char *)0;
   
   //for (int i=0; i<=2+argc; ++i) {            //    For debugging
   //    printf("%s\n",arguments[i]); }
   //while (true) {}
   
   // replace current process by a new one running our application
   execvp("javaw.exe", arguments);
  
   return 0; 
} 

