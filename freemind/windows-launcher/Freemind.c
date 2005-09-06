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

   int no_of_fixed_arguments = 4;
   int one_for_stopping_null = 1;
   int no_of_passed_arguments_without_caller = argc - 1;
   char *application_name = "lib\\freemind.jar";
   char **arguments = (char **) malloc(( no_of_fixed_arguments +
                                         no_of_passed_arguments_without_caller + 
                                         one_for_stopping_null) * sizeof(char*));

   // Pick the path from argv[0]. This is for the case that the launcher is not
   // started from the folder in which it resides.

   char *full_name = application_name;
   if (char *pos = strrchr(argv[0],'\\')) {
      int prefix_length = pos - argv[0] + 1;
      full_name = (char *) malloc((prefix_length + strlen(application_name) +
                                   one_for_stopping_null ) * sizeof(char));
      memcpy(full_name,argv[0],prefix_length);
      strcpy(full_name + prefix_length, application_name); }
      
   arguments[0] = "javaw.exe";
   arguments[1] = "-Xmx256M";   // Allow Java to consume as much as 256 MB of memory
   arguments[2] = "-jar";
   arguments[3] = surround_by_quote(full_name);

   // Surround all the arguments passed by quote
   
   for (int i=1; i <= no_of_passed_arguments_without_caller; ++i) {
      arguments[no_of_fixed_arguments + i] = surround_by_quote(argv[i]); }

   // Null-terminate the arguments array

   arguments[no_of_fixed_arguments + no_of_passed_arguments_without_caller] = (char *)0;
   
   if (1 == 0) { //    For debugging
      for (int i=0; i < no_of_fixed_arguments + no_of_passed_arguments_without_caller; ++i) {
         printf("Argument %s\n",arguments[i]); }}
   
   // Replace current process by a new one running our application

   execvp("javaw.exe", arguments);
  
   return 0; 
}
