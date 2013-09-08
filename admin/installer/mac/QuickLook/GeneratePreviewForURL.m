#include <CoreFoundation/CoreFoundation.h>
#include <CoreServices/CoreServices.h>
#include <QuickLook/QuickLook.h>
#include <Cocoa/Cocoa.h>
#include "Common.h"


OSStatus GeneratePreviewForURL(void *thisInterface, QLPreviewRequestRef preview, CFURLRef url, CFStringRef contentTypeUTI, CFDictionaryRef options)
{
//	freopen([@"/Users/dvsekhvalnov/tmp/freemindql.log" cStringUsingEncoding:NSASCIIStringEncoding],"a+",stderr);
	frlog(@"++ GeneratePreviewForURL");
	
	NSAutoreleasePool *pool;
	
    pool = [[NSAutoreleasePool alloc] init];
	
	NSString *tempdir=createTempDir();
	NSString *outFile=[tempdir stringByAppendingPathComponent:@"out.png"];
	
	NSString *inFile=[[url absoluteURL] path];	
	
	frlog(@"Input file is %@",inFile);
		
	NSBundle *main=[NSBundle bundleWithIdentifier:@"net.freemind.qlgenerator"];
	
	frlog(@"main bundle:%@",main);
	
	NSString *freemindLocation=[[NSBundle bundleWithIdentifier:@"net.freemind.qlgenerator"] objectForInfoDictionaryKey:@"FreemindPath"];
	
	frlog(@"freemind location is:%@",freemindLocation);
	
	NSString *freemindJar=[NSString stringWithFormat:@"%@/Contents/Java/freemind.jar",freemindLocation];

	frlog(@"freemind jar is:%@",freemindJar);

	NSString *javaOpts=[[NSBundle bundleWithIdentifier:@"net.freemind.qlgenerator"] objectForInfoDictionaryKey:@"JVMOptions"];
	
	frlog(@"JVM options is:%@",javaOpts);
	
	NSArray *args=[NSArray arrayWithObjects:
				   javaOpts,
				   @"-cp", freemindJar,
				   @"freemind.view.mindmapview.IndependantMapViewCreator",
				   inFile,outFile,
				   nil];
	
	NSDictionary *env=[[[NSDictionary alloc] init] autorelease];
	
	int status;
	runTask(args,tempdir,env,&status);
	
	if (QLPreviewRequestIsCancelled(preview))
        return noErr;
	
	if (status != 0) 
	{
        NSLog(@"FreemindQL: failed with exit code %d.", status);
    }
	else
	{
		NSDate *image=[NSData dataWithContentsOfFile:outFile];
		
		frlog(@"image loaded");
		
		NSDictionary *props=[[[NSDictionary alloc] init] autorelease];
		QLPreviewRequestSetDataRepresentation(preview,
											  (CFDataRef)image,
											  kUTTypeImage,(CFDictionaryRef)props);		
	}
	
	frlog(@"About to release pool");
	
    [pool release];	
	
	frlog(@"-- GeneratePreviewForURL");	
    return noErr;
}

void CancelPreviewGeneration(void* thisInterface, QLPreviewRequestRef preview)
{
    // implement only if supported
}
