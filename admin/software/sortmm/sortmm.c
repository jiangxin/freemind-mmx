#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <libxml/parser.h>
#include <libxml/tree.h>

static void xmlTraverse(xmlNode *node);
static void xmlSort(xmlNode *node);
static int xmlListCompare(xmlNode *node1, xmlNode *node2);

// sortmm
int main(int argc, char **argv) {
	if (argc < 1) {
		printf("usage: sortmm <input> > <output>");
		return 1;
	}

	xmlDoc *doc;

	// it matters to place the
	// following method exactly
	// here for proper operation
	xmlKeepBlanksDefault(0);

	xmlInitParser();

	doc = xmlParseFile(argv[1]);

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
		if (node->type == XML_TEXT_NODE) {
			// remove text nodes
			// xmlUnlinkNode(node);
			// xmlFreeNode(node);
		}

		if (node->type == XML_ELEMENT_NODE) {
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
	xmlList *list;
	xmlLink *link;
	xmlNode *lnode;

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
		if (node->type == XML_ELEMENT_NODE) {
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
		if (node->type == XML_ELEMENT_NODE) {
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
	xmlChar *str1;
	xmlChar *str2;
	int val;

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

	return val;
}
