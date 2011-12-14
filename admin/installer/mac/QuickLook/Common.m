#import "Common.h"

void runTask(NSArray *args, NSString *workDir, NSDictionary *env, int *exitCode) {
    NSTask *task = [[NSTask alloc] init];
    [task setCurrentDirectoryPath:workDir];     
    [task setEnvironment:env];
    [task setLaunchPath:@"/usr/bin/java"];
    [task setArguments:args];
    
    NSPipe *pipe;
    pipe = [NSPipe pipe];
    [task setStandardOutput: pipe];

    [task launch];
    [task waitUntilExit];
    
    *exitCode = [task terminationStatus];
	
    [task release];    
}


NSString* createTempDir()
{
	NSString *timestamp=[[NSString alloc] initWithFormat:@"%0.f",[NSDate timeIntervalSinceReferenceDate]*1000.0];
	NSString *path=[[NSString alloc] initWithFormat:@"freemind/%@",timestamp];	
	NSString *outputDir=[NSTemporaryDirectory() stringByAppendingPathComponent: path];
	
	NSError *error=[[NSError alloc] init];
	
	BOOL success=[[NSFileManager defaultManager] createDirectoryAtPath:outputDir 
										   withIntermediateDirectories:YES 
															attributes:nil 
																 error:&error];		
	[timestamp release];
	[error release];
	[path release];
	
	if(!success)
	{
		return nil;
	}	
	
	return outputDir;
}
