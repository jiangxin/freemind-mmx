#include <process.h> 
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>

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
   char *application_name = "lib\\freemind.jar";
   char *standard_javaw_path = "javaw.exe";
   char *alternative_javaw_path = "jre\\bin\\javaw.exe";
   char *argument_allowing_more_memory = "-Xmx256M";
   int /*bool*/ take_standard_javaw_path = 1; // 1 - true; 0 - false.

   int one_for_stopping_null = 1;
   int no_of_passed_arguments_without_caller = argc - 1;
   char *javaw_path = take_standard_javaw_path ? standard_javaw_path : alternative_javaw_path;
   
   char **arguments = (char **) malloc(( no_of_fixed_arguments +
                                         no_of_passed_arguments_without_caller + 
                                         one_for_stopping_null) * sizeof(char*));

   // Pick the path from argv[0]. This is for the case that the launcher is not
   // started from the folder in which it resides.

   char *path_to_launcher = argv[0];
   if (char *position_of_last_occurrence = strrchr(path_to_launcher,'\\')) {
      int prefix_length = position_of_last_occurrence - path_to_launcher + 1;

      char *path_to_launcher_without_file = (char *) malloc((prefix_length +
                                             one_for_stopping_null ) * sizeof(char));
      strncpy(path_to_launcher_without_file, path_to_launcher, prefix_length);
      path_to_launcher_without_file[prefix_length] = '\0'; // End the string with null.
      
      chdir( path_to_launcher_without_file );
   }
      
   arguments[0] = javaw_path; 
   arguments[1] = argument_allowing_more_memory;
   arguments[2] = "-jar";
   arguments[3] = surround_by_quote(application_name);

   // Surround all the arguments passed by quote
   
   for (int i=1; i <= no_of_passed_arguments_without_caller; ++i) {
      arguments[no_of_fixed_arguments + i - 1] = surround_by_quote(argv[i]); }

   // Null-terminate the arguments array

   arguments[no_of_fixed_arguments + no_of_passed_arguments_without_caller] = (char *)0;
   
   if (1 == 0) { //    For debugging
      for (int i=0; i < no_of_fixed_arguments + no_of_passed_arguments_without_caller; ++i) {
         printf("Argument %s\n",arguments[i]); }}
   
   // Replace current process by a new one running our application

   execvp(javaw_path, arguments);
   // the following patch seems useful for vista but needs additional testing.
   // https://sourceforge.net/tracker/?func=detail&atid=107118&aid=2350483&group_id=7118
   // Submitted By: Mario Valle (mvalle58)
   // Summary: Windows launcher does nothing (+ solution)
   // _spawnvp(_P_DETACH, arguments[0], arguments);
  
   return 0; 
}
