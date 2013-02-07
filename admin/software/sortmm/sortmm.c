// sortmm
//
// 2013 - Ali Akcaagac <aliakc@web.de>
//
// 24.01.2013: initial public release
// 28.01.2013: improve sorting of node elements
// 29.01.2013: improve error checking and parsing

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <libxml/parser.h>
#include <libxml/tree.h>

#define TRUE 1
#define FALSE 0

static void xmlTraverse(xmlNode *node);
static void xmlSort(xmlNode *node);
static int xmlListCompare(xmlNode *node1, xmlNode *node2);
static int xmlListCompareD(xmlNode *node1, xmlNode *node2);

// sortmm
int main(int argc, char **argv) {
	xmlDoc *doc = NULL;

	if (argc < 1) {
		fprintf(stderr, "usage: sortmm <input> > <output>\n");

		return -1;
	}

	// it matters to place the
	// following method exactly
	// here for proper operation
	xmlKeepBlanksDefault(0);

	xmlInitParser();

	doc = xmlParseFile(argv[1]);

	if (doc == NULL) {
		fprintf(stderr, "error: unable to parse file %s\n", argv[1]);

		xmlCleanupParser();
		xmlMemoryDump();

		return -1;
	}

	xmlTraverse(doc->children);

	xmlDocFormatDump(stdout, doc, 1);
	xmlFreeDoc(doc); 

	xmlCleanupParser();
	xmlMemoryDump();

	return 0;
}

// xmlTraverse
static void xmlTraverse(xmlNode *node) {
	while (node != NULL) {
		// make sure we sort only node elements and not node <any> elements
		if (node->type == XML_ELEMENT_NODE
		    && (xmlStrEqual(node->name, "map") == TRUE
			|| xmlStrEqual(node->name, "node") == TRUE)) {
			if (node->children != NULL) {
				// node recursion
				xmlTraverse(node->children);
			}

			// node sort
			xmlSort(node);
		}

		node = node->next;
	}
}

static void xmlSort(xmlNode *node) {
	xmlList *list = NULL;
	xmlLink *link = NULL;
	xmlNode *lnode = NULL;

	list = xmlListCreate(NULL, (xmlListDataCompare) xmlListCompare);

	// rewind to first sibling
	while (node != NULL) {
		if (node->prev == NULL) {
			break;
		}

		node = node->prev;
	}

	// forward to next sibling
	while (node != NULL) {
		// make sure we sort only node elements and not node <any> elements
		if (node->type == XML_ELEMENT_NODE
		    && xmlStrEqual(node->name, "node") == TRUE) {
			xmlListInsert(list, node);
		}

		if (node->next == NULL) {
			break;
		}

		node = node->next;
	}

	// rewind to first sibling
	while (node != NULL) {
		if (node->prev == NULL) {
			break;
		}

		node = node->prev;
	}

	// forward to next sibling
	while (node != NULL) {
		// make sure we sort only node elements and not node <any> elements
		if (node->type == XML_ELEMENT_NODE
		    && xmlStrEqual(node->name, "node") == TRUE) {
			// get node link from list
			link = xmlListFront(list);
			// get node address from list
			lnode = xmlLinkGetData(link);
			// add node to previous sibling
			xmlAddPrevSibling(node, lnode);
			// pop node link from list
			xmlListPopFront(list);
		}

		if (node->next == NULL) {
			break;
		}

		node = node->next;
	}

	xmlListDelete(list);
}

// xmlListCompare
static int xmlListCompare(xmlNode *node1, xmlNode *node2) {
	xmlChar *str1 = NULL;
	xmlChar *str2 = NULL;
	int val = 0;

	if (node1 == node2) {
		return 0;
	}

	if (node1 == NULL) {
		return -1;
	}

	if (node2 == NULL) {
		return 1;
	}

	// properties can't be NULL here
	str1 = xmlGetProp(node1, "TEXT");
	str2 = xmlGetProp(node2, "TEXT");

	// ascending
	val = xmlStrcmp(str1, str2);

	// descending
	// val = xmlStrcmp(str2, str1);

	// properties must be free'd
	xmlFree(str1);
	xmlFree(str2);

	// don't touch dupe nodes
	// keep them ordered not sorted
	if (val == 0) {
		// needs more work
		// return xmlListCompareD(node1, node2);

		return -1;
	}

	return val;
}

// xmlListCompareD
static int xmlListCompareD(xmlNode *node1, xmlNode *node2) {
	xmlChar *str1 = NULL;
	xmlChar *str2 = NULL;
	int val = 0;

	while (node1->children != NULL) {
		// make sure we sort only node elements and not node <any> elements
		if (node1->children->type == XML_ELEMENT_NODE
		    && xmlStrEqual(node1->children->name, "attribute") == TRUE) {
			str1 = xmlGetProp(node1->children, "NAME");

			if (xmlStrEqual(str1, "Start") == TRUE) {
				// properties must be free'd
				xmlFree(str1);

				// properties can't be NULL here
				str1 = xmlGetProp(node1->children, "VALUE");
				break;
			}

			// properties must be free'd
			xmlFree(str1);
		}

		node1->children = node1->children->next;
	}

	while (node2->children != NULL) {
		// make sure we sort only node elements and not node <any> elements
		if (node2->children->type == XML_ELEMENT_NODE
		    && xmlStrEqual(node2->children->name, "attribute") == TRUE) {
			str2 = xmlGetProp(node2->children, "NAME");

			if (xmlStrEqual(str2, "Start") == TRUE) {
				// properties must be free'd
				xmlFree(str2);

				// properties can't be NULL here
				str2 = xmlGetProp(node2->children, "VALUE");
				break;
			}

			// properties must be free'd
			xmlFree(str2);
		}

		node2->children = node2->children->next;
	}

	// ascending
	val = xmlStrcmp(str1, str2);

	// descending
	// val = xmlStrcmp(str2, str1);

	// properties must be free'd
	xmlFree(str1);
	xmlFree(str2);

	// don't touch dupe nodes
	// keep them ordered not sorted
	if (val == 0) {
		return -1;
	}

	return val;
}
