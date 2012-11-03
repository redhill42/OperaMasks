/// <summary>HTML Syntax highlighting methods in JavaScript.</summary>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>

/// <summary>Handles exceptions</summary>
/// <param name="exception">a catched exception</param>
/// <code>
///	try
///	{	
///		// returns false if failed
///		if (!doSomething())
///			throw "Could not do anything";
///	}
///	catch (exception)
///	{	
///		handleException(exception);
///	}
/// </code>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function handleException( exception )
{
	if (typeof(exception) == "string")
		alert("Error: "+ exception);
	else if (exception.description == null )
		alert("Error: "+ exception.message );
	else
		alert("Error: "+ exception.description );
//	Response.Write("<b>Error in script: " + exception + "</b></br>");
}

function createDOMDocument() {
    var result ;
    if(document.implementation && document.implementation.createDocument) {
    var   ex;
//    Attr.prototype.__proto__.__defineGetter__( "nodeTypedValue" ,  function (){
//        return this.nodeValue ;
//    });
    XMLDocument.prototype.__proto__.__defineGetter__( "xml" ,  function (){
         try {
             return   new  XMLSerializer().serializeToString( this );
        } catch (ex){
             var  d  =  document.createElement( "div" );
            d.appendChild( this .cloneNode( true ));
             return  d.innerHTML;
        }
    });
    Element.prototype.__proto__.__defineGetter__( "xml" ,  function (){
         try {
             return   new  XMLSerializer().serializeToString( this );
        } catch (ex){
             var  d  =  document.createElement( "div" );
            d.appendChild( this .cloneNode( true ));
             return  d.innerHTML;
        }
    });
    XMLDocument.prototype.__proto__.__defineGetter__( "text" ,  function (){
         return   this .firstChild.textContent
    });
    Element.prototype.__proto__.__defineGetter__( "text" ,  function (){
         return   this .textContent
    });




    XMLDocument.prototype.selectSingleNode = Element.prototype.selectSingleNode = function (xpath){
         var  x = this.selectNodes(xpath)
         if ( ! x  ||  x.length < 1 ) return   null ;
         return  x[ 0 ];
    }
    XMLDocument.prototype.selectNodes = Element.prototype.selectNodes = function (xpath){
         var  xpe  =   new  XPathEvaluator();
         var  nsResolver  =  xpe.createNSResolver( this .ownerDocument  ==   null   ?
             this .documentElement :  this .ownerDocument.documentElement);
         var  result  =  xpe.evaluate(xpath,  this , nsResolver,  0 ,  null );
         var  found  =  [];
         var  res;
         while  (res  =  result.iterateNext())
            found.push(res);
         return  found;
    }
    result = document.implementation.createDocument("", "", null);  
    }
    else {
    result = new ActiveXObject("Msxml2.DOMDocument");
    }
    return result ;
}

/// <summary>Loads an xml file</summary>
/// <param name="sFileName">XML file name</param>
/// <returns>a DOMDocument object ( i.e. a ActiveXObject("Msxml2.DOMDocument") ) </returns>
/// <exception>If file not loaded successfully</exception>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function loadXML( sFileName)
{
    
	var xmlDoc = createDOMDocument() ;
	xmlDoc.async = false;
    try
	{	
		// try loading xml file, throw exception if failed
		if (!xmlDoc.load( sFileName ))
			throw "Could not load xml file " + sFileName;
	}
	catch (exception)
	{	
		xmlDoc=null;
		handleException(exception);
	}
	
	return xmlDoc;
};

/// <summary>adds a CDATA child elem</summary>
/// <param name="node">node to append child</param>
/// <param name="nodeName">new child node name</param>
/// <param name="cdata">CDATA value</param>
/// <exception>If could not create child node</exception>
/// <exception>If could not create CDATA node</exception>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function addChildCDATAElem( node, nodeName, cdata )
{
	var newNode = node.ownerDocument.createElement( nodeName);
	if (newNode == null)
		throw "Could not append node to " + node.nodeName;		
	node.appendChild( newNode );
	
	var newCDATANode = node.ownerDocument.createCDATASection( cdata );
	if (newCDATANode == null)
		throw "Could not append CDATA node to " + newNode.nodeName;
	newNode.appendChild( newCDATANode );
}

/// <summary>adds a text child elem</summary>
/// <param name="node">node to append child</param>
/// <param name="nodeName">new child node name</param>
/// <param name="text">text value</param>
/// <exception>If could not create child node</exception>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function addChildElem( node, nodeName, text )
{
	var newNode = node.ownerDocument.createElement( nodeName);
	if (newNode == null)
		throw "Could not append node to " + node.nodeName;		
	newNode.text = text;
	node.appendChild( newNode );
}

/// <summary>Adds \ to regular expression character</summary>
/// <param name="char0">character to transform</param>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function stringToRegExp( char0 )
{
	var regExp = /(\-|\+|\*|\?|\(|\)|\[|\]|\\|\$|\^|\!)/g; 

	return char0.replace(regExp, "\\$1");
}

/// <summary>Builds keywords family regular expressions</summary>
/// <param name="languageNode"><see also cref="XMLDOMNode"/> language node</para>
/// <remarks>This method create regular expression that match a whole keyword family and 
///	add it as a parameter "regexp" to the keywordlist node.</remarks>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function buildKeywordRegExp( languageNode )
{
	var keywordListList,keywordListNode;
	var sRegExp,preNode, postNode;
	var kwList, kwNode,rootNode;
	
	rootNode = languageNode.selectSingleNode("/*");

	// iterating keywords	
	keywordListList = rootNode.selectNodes("keywordlists/keywordlist");
	keywordListList.reset();
	for ( keywordListNode = keywordListList.nextNode(); keywordListNode != null; keywordListNode= keywordListList.nextNode() )
	{
		sRegExp="\\b";
		
		// adding pre...
		preNode = keywordListNode.attributes.getNamedItem("pre");
		if (preNode != null)
			sRegExp=sRegExp+preNode.nodeTypedValue;
		
		sRegExp=sRegExp+"(";
		
		// build regular expression...
		kwList = keywordListNode.selectNodes("kw");
		kwList.reset();
		// iterate kw elements
		for (kwNode = kwList.nextNode() ; kwNode != null; kwNode = kwList.nextNode() )
		{
			sRegExp=sRegExp +  stringToRegExp( kwNode.nodeTypedValue ) + "|"; 
		}
		
		// close string
		if (sRegExp.length > 1)
			sRegExp=sRegExp.substring(0,sRegExp.length-1);

		sRegExp=sRegExp+")";
		// adding pre...
		postNode = keywordListNode.attributes.getNamedItem("post");
		if (postNode != null)
			sRegExp=sRegExp+postNode.nodeTypedValue;
			
		sRegExp=sRegExp+"\\b";
		
		// add to keywordListNode
		keywordListNode.setAttribute( "regexp", sRegExp );
	}

}

/// <summary>Builds regular expression out of contextNode</summary>
/// <param name="languageNode"><see also cref="XMLDOMNode"/> language node</para>
/// <param name="contextNode"><see also cref="XMLDOMNode"/> context node</para>
/// <remarks>This method create regular expression that match all the context rules
/// add it as a parameter "regexp" to the context node.</remarks>
/// <exception>If keyword family not corresponding to keyword attribute.</exception>
/// <exception>Regular expression rule missing regexp argument</exception>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function buildRuleRegExp( languageNode, contextNode )
{
	var sRegExp, ruleNode, regExpExprNode, rootNode;
	var keywordListNode, keywordListNameNode, keywordListRegExpNode,xp;
	
	rootNode = languageNode.selectSingleNode("/*");
	sRegExp="(";

	var ruleList=contextNode.childNodes;
	// building regular expression	
	for (ruleNode=ruleList.nextNode(); ruleNode != null; ruleNode=ruleList.nextNode() )
	{
		if (ruleNode.nodeName == "#comment")
			continue;
			
		// apply rule...
		if (ruleNode.nodeName == "detect2chars")
		{
			var char0=ruleNode.attributes.getNamedItem("char").value;
			var char1=ruleNode.attributes.getNamedItem("char1").value;
			sRegExp= sRegExp + stringToRegExp( char0 + char1 ) + "|";
		}
		else if (ruleNode.nodeName == "detectchar")
		{
			var char0=ruleNode.attributes.getNamedItem("char").value;
			sRegExp=sRegExp + stringToRegExp( char0 ) + "|";
		}
		else if (ruleNode.nodeName == "linecontinue")
		{
			sRegExp=sRegExp + "\n|"
		}
		else if (ruleNode.nodeName == "regexp" )
		{
			regExpExprNode = ruleNode.attributes.getNamedItem("expression");
			if ( regExpExprNode == null )
				throw "Regular expression rule missing expression attribute";
				
			sRegExp=sRegExp + regExpExprNode.nodeTypedValue + "|";
		}
		else if (ruleNode.nodeName == "keyword")
		{
			// finding keywordlist
			keywordListNameNode = ruleNode.attributes.getNamedItem("family");
			if (keywordListNameNode == null)
				throw "Keyword rule missing family";
			xp="keywordlists/keywordlist[@id=\""
					+ keywordListNameNode.nodeTypedValue 
					+ "\"]";
			keywordListNode = rootNode.selectSingleNode(xp);
			if (keywordListNode == null)
				throw "Could not find keywordlist (xp: "+ xp + ")";
				
			keywordListRegExpNode = keywordListNode.attributes.getNamedItem("regexp");
			if (keywordListRegExpNode == null)
				throw "Could not find keywordlist regular expression";
				
			// adding regexp
			sRegExp=sRegExp+keywordListRegExpNode.nodeTypedValue+"|";
		}
	}

	if (sRegExp.length > 1)
		sRegExp=sRegExp.substring(0,sRegExp.length-1)+")";
	else
		sRegExp="";
	
	return sRegExp;	
};

/// <summary>Precompiles regular expressions, search strings and prepares rules attribute</summary>
/// <param name="xmlDoc"><seealso DOMDocument/> highlight syntax document</param>
/// <param name="languageNode"><see also cref="XMLDOMNode"/> context node</para>
/// <exception>If rule id not corresponding to a rule family</exception>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function buildRules( languageNode )
{
	var contextList, contextNode, sRegExp, rootNode;	
	var rulePropList, rulePropNode, rulePropNodeAttributes, ruleList, ruleNode;

	rootNode = languageNode.selectSingleNode("/*");
	
	// first building keyword regexp
	buildKeywordRegExp( languageNode );	
	
	contextList = languageNode.selectNodes("contexts/context");
	// create regular expressions for context
	for (contextNode = contextList.nextNode(); contextNode != null; contextNode = contextList.nextNode())
	{
		sRegExp = buildRuleRegExp( languageNode, contextNode );
		// add attribute
		contextNode.setAttribute( "regexp", sRegExp );	
	}
}

/// <summary>Prepares syntax xml file</summary>
/// <param name="xmlDoc">xml Syntax</param>
/// <returns><seealso cref"DOMDocument"> language description </returns>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function loadAndBuildSyntax( xmlDoc )
{
	var xmlModDoc, languageNode, languageNodeList;
	var needBuildNode, bNeedBuild;

	// check if build needed...
	bNeedBuild = true;
	needBuildNode = xmlDoc.documentElement.selectSingleNode("/highlight").attributes.getNamedItem("needs-build");
	if (needBuildNode == null  || needBuildNode.nodeTypedValue=="yes")
	{
		// iterate languages and prebuild
		languageNodeList = xmlDoc.documentElement.selectNodes("/highlight/languages/language");
		languageNodeList.reset();
		for(languageNode = languageNodeList.nextNode(); languageNode != null; languageNode = languageNodeList.nextNode())
		{
			/////////////////////////////////////////////////////////////////////////		
			// build regular expressions
			buildRules( languageNode );	
		}

		// updating...
		xmlDoc.documentElement.selectSingleNode("/highlight").setAttribute("needs-build","no");
	}
	
	// save file if asked
	saveBuildNode = xmlDoc.documentElement.selectSingleNode("/highlight").attributes.getNamedItem("save-build");
	if (saveBuildNode != null && saveBuildNode.nodeTypedValue == "yes")
		xmlDoc.save( sXMLSyntax );
		
	// closing file
	return xmlDoc;
}

/// <summary>Finds the rule that trigerred the match</summary>
/// <param name="languageNode"><see also cref="XMLDOMNode"/> language node</para>
/// <param name="contextNode"><see also cref="XMLDOMNode"/> context node</para>
/// <param name="sMatch"><see also cref="String/> that matched the context regular expression</param>
/// <remarks>If the <seealso RegExp/> finds a rule occurence, this method is used to find which rule has been trigerred.</remarks>
/// <exception>Triggers if sMatch does not match any rule of contextNode</exception>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function findRule( languageNode, contextNode, sMatch )
{
	var regExpNode, regExp, sRegExp, arr, familyNode,xp;
	var ruleNode, regExpExprNode,rootNode;
	var ruleList=contextNode.childNodes;
	
	rootNode=languageNode.selectSingleNode("/*");

	// building regular expression	
	for (var i = 0 ; i<ruleList.length ; i++ )
	//for (ruleNode=ruleList.nextNode(); ruleNode != null ; ruleNode = ruleList.nextNode() )
	{
	    ruleNode = ruleList[i] ;
		if (ruleNode.nodeName == "#comment")
			continue;
	
		if (ruleNode.nodeName == "detect2chars")
		{
			var char0=ruleNode.attributes.getNamedItem("char").value;
			var char1=ruleNode.attributes.getNamedItem("char1").value;
			if ( sMatch == char0 + char1)			
				return ruleNode;
		}
		else if (ruleNode.nodeName == "detectchar")
		{
			var char0=ruleNode.attributes.getNamedItem("char").value;
			if (char0 == sMatch)
				return ruleNode;
		}
		else if (ruleNode.nodeName == "linecontinue")
		{
			if ( "\n" == sMatch)
				return ruleNode;
		}
		else if (ruleNode.nodeName == "regexp")
		{
			regExpExprNode=ruleNode.attributes.getNamedItem("expression");
			if ( regExpExprNode == null )
				throw "Regular expression rule missing expression attribute";
			
			regExp = new RegExp( regExpExprNode.value, "m" );
			arr = regExp.exec(sMatch);
			if ( arr != null )
				return ruleNode;
		}	
		else if (ruleNode.nodeName == "keyword")
		{
			familyNode = ruleNode.attributes.getNamedItem("family");
			if ( familyNode == null)
				throw "Could not find family attribute for keyword";
			xp="keywordlists/keywordlist[@id=\"" 
					+ familyNode.nodeTypedValue 
					+ "\"]/@regexp";
			regExpNode = rootNode.selectSingleNode( xp );
			if ( regExpNode == null)
				throw "Could not find regular expression for keyword family "+ ruleNode.attributes.getNamedItem("attribute").value + "(xp: "+xp+")";

			// estimate regular expression	
			sRegExp="(" + regExpNode.nodeTypedValue + ")";
			regExp = new RegExp( sRegExp, "m" );
			arr=regExp.exec(sMatch);
			if ( arr != null )
				return ruleNode;
		}
	}
	return null;
}

/// <summary>Applies the context rules succesively to sString</summary>
/// <param name="languageNode"><see also cref="XMLDOMNode"/> language node</para>
/// <param name="contextNode"><see also cref="XMLDOMNode"/> context node</para>
/// <param name"sString">String to parse and convert</param>
/// <param name="parsedCodeNode"><seealso cref="XMLDOMNode">mother node for dumping parsed code</param>
/// <remarks>This methods uses the pre-computed regular expressions of context rules, rule matching, etc...
/// the result is outputted in the xmlResult document, starting at parsedCodeNode node.
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function applyRules( languageNode, contextNode, sString, parsedCodeNode)
{
	var regExp, arr,sRegExp;
	var ruleNode,newNode, newCDATANode;

	// building regExp 
	sRegExp=contextNode.attributes.getNamedItem("regexp").value;
	var regExp = new RegExp( sRegExp, "m" );

	while (sString.length > 0)
	{
		// apply
		arr = regExp.exec( sString );
		if (arr == null)
		{
			addChildCDATAElem( parsedCodeNode,
							contextNode.attributes.getNamedItem("attribute").value, 
							sString );
			
			// finished parsing
			regExp=null;
			return null;
		}
		else
		{
		//alert( contextNode.attributes.getNamedItem("attribute").nodeTypedValue ) ;
			// adding text
			addChildCDATAElem(parsedCodeNode, 
							contextNode.attributes.getNamedItem("attribute").value,
							sString.substring(0, arr.index ) );
			
			// find rule...
			ruleNode = findRule( languageNode, contextNode, arr[0] );
			if (ruleNode == null)
				throw "Didn't matching rule, regular expression false ? ( context: " + contextNode.attributes.getNamedItem("id").value;
			
			// check if rule nees to be added to result...
			attributeNode=ruleNode.attributes.getNamedItem("attribute");
			if (attributeNode != null && attributeNode.value!="hidden" )
			{
				addChildCDATAElem(parsedCodeNode,
								ruleNode.attributes.getNamedItem("attribute").value ,
								arr[0]);
			}
			
			// update context if necessary
			if ( contextNode.attributes.getNamedItem("id").value != ruleNode.attributes.getNamedItem("context").value )
			{
				// return new context 
				var xpContext = "contexts/context[@id=\"" 
								+ ruleNode.attributes.getNamedItem("context").value
								+ "\"]";
				contextNode = languageNode.selectSingleNode( xpContext);
				if (contextNode == null)
					throw "Didn't matching context, error in xml specification ?";
					
				// build new regular expression
				sRegExp=contextNode.attributes.getNamedItem("regexp").value;
				regExp = new RegExp( sRegExp, "m" );
			}
			sString = sString.substring(arr.index+arr[0].length, sString.length);			
		}
	}
	regExp = null;
}


/// <summary>Create and populate an xml document with the corresponging language</summary>
/// <param name="xmlDoc"><seealso DOMDocument/> highlight syntax document</param>
/// <param name="sLang">language string description. For C++, use cpp.</param> 
/// <param name="sRootTag">Root tag (under parsed code) for the generated xml tree.</param> 
/// <param name="bInBox>true if in box</param>
/// <param name="sCode">Code to parse</param>
/// <returns><seealso cref="DOMDocument"> document containing parsed node.</returns>
/// <remarks>This method builds an XML tree containing context node. Use an xsl file to render it.</remarks>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function buildHighlightTree( xmlDoc, sLang, sRootTag, bInBox, sCode )
{
	var languageAttribute,languageNode,xp;
	var resultMainNode;
	var sHighlightedCode, sDefault;

	try
	{			
		/////////////////////////////////////////////////////////////////////////		
		// getting language
		xp="/highlight/languages/language[@id=\"" + sLang + "\"]";
		languageNode=xmlDoc.documentElement.selectSingleNode( xp );
		if (languageNode == null)
			throw "Could not find " + sLang + "language (xpath: " + xp + ")";
	
		/////////////////////////////////////////////////////////////////////////		
		// getting context
		contextsNode=languageNode.selectSingleNode( "contexts" );
		if (contextsNode == null)
			throw "Could not find contexts node for " + sLang + "language";

		/////////////////////////////////////////////////////////////////////////		
		// getting default context	
		//alert( contextsNode.attributes.getNamedItem("default").value ) ;
		sDefault=contextsNode.attributes.getNamedItem("default").value;
		xp="context[@id=\"" +  sDefault + "\"]";
		contextNode=contextsNode.selectSingleNode( xp );
		if (contextNode == null)
			throw "Could not find default context for " + sLang + "language (xpath: " + xp + ")";
	
		// create result xml
		xmlResult = createDOMDocument() ;

		///////////////////////////////////////////////////////////////////////////	
		// creating main node
		resultMainNode=xmlResult.createElement( "parsedcode" );
		if (resultMainNode == null)
			throw "Could not create main node parsedcode";
		xmlResult.appendChild(resultMainNode);
					
		///////////////////////////////////////////////////////////////////////////	
		// adding language attribute
		resultMainNode.setAttribute("lang", sRootTag );
		resultMainNode.setAttribute("in-box", bInBox );

		///////////////////////////////////////////////////////////////////////////	
		// parse and populate xmlResult
		applyRules( languageNode, contextNode, sCode, resultMainNode);

		return xmlResult;
	}
	catch(exception)
	{
		handleException (exception);
		xmlResult=null;
		return null;
	}
}

/// <summary>Apply syntax matching to sCode with the corresponding language sLang</summay>
/// <param name="sLang">language string description. For C++, use cpp.</param> 
/// <param name="sRootTag">Root tag (under parsed code) for the generated xml tree.</param> 
/// <param name="sCode">Code to parse</param>
/// <returns>the highlighted code.</returns>
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function highlightCode( sLang, sRootTag, bInBox, sCode)
{
	var xmlResult, sResult;

	try
	{		

		// re-build highlight tree	
		xmlResult = buildHighlightTree( xmlGlobDoc, sLang, sRootTag, bInBox, sCode );
		// render xml
       if(document.implementation && document.implementation.createDocument) {
       var xslProc = new XSLTProcessor();
        xslProc.importStylesheet(xslGlobDoc);
		sResult=xslProc.transformToFragment(xmlResult, document) ;
       }
       else {
		sResult=xmlResult.transformNode( xslGlobDoc );	
       }
	}
	catch(exception)
	{
		handleException (exception);
		xmlResult=null;
		return "";
	}
	finally
	{
		xmlResult=null;
		return sResult;
	}	
};

/// <summary>Find the lang in the tag</summary>
/// <param name="sMatch">a string</param>
/// <returns>the value of the parameter corresponding to sGlobDefaultLong</returns>
function findLang( sMatch )
{
	var sRegExp, regExp, arr;
	
	// build regular expression
	sRegExp= sGlobLangTag + "\\s*=(\"[a-z]+\"|[a-z]+)";
	regExp = new RegExp( sRegExp, "im"); 

	arr = regExp.exec( sMatch );
	if (arr==null || arr.length < 2 )
		return null;
	else
	{	
		if (arr[1].charAt(0)=="\"")
			return arr[1].substring(1, arr[1].length-1);
		else
			return arr[1];
	}
}

/// <summary> Helper function to be used in String::Replace</summary>
/// <param name="sMatch">Full match ($0)</param>
/// <param name="sValue">text inside tags ($1)</param>
function replaceByCode( sMatch, sValue )
{
	var sLang, sTemplate, xp, languageNode;
	
	// get language
	sLang = findLang( sMatch );
	// if no language... do nothing
	if (sLang == null)
		return sMatch;
	// find language in language file if not found return text...
	xp="/highlight/languages/language[@id=\"" + sLang + "\"]";
	languageNode=xmlGlobDoc.documentElement.selectSingleNode( xp );
	if (languageNode == null)
		return sMatch;
	
	//highlight code
	sTemplate = sLang;
	
	return highlightCode( sLang, sTemplate, bGlobCodeInBox, sValue);
}
			
/// <summary>Processes HTML and highlight code in <pre>...</pre> and in <code>...</code></summary>
/// <param name="sValue">HTML code</param>
/// <param name="sOT">character opening tag: usually &lt;</param>
/// <param name="sTag">tag containing code</param>
/// <param name="sCT">character closing tag: usually &gt;</param>
/// <param name="bInBox">boolean: true if should be in box, false otherwize</param>
/// <returns>HTML with colored code</returns>
/// Available languages: C++ -> cpp, JSCript -> jscript, VBScript -> vbscript
/// <remarks>Author: Jonathan de Halleux, dehalleux@pelikhan.com, 2003</remarks>
function processAndHighlightText( sValue, sOT, sTag, sCT, bInBox )
{
	var sRegExp, regExp;
	// <pre lang="cpp">using</pre>
	
	// setting global variables
	sGlobOT = sOT;
	sGlobCT = sCT;
	sGlobTag = sTag;
	bGlobCodeInBox=bInBox;
		
	// building regular expression
	sRegExp = sGlobOT 
		+"\\s*"
		+ sGlobTag
		+".*?"
		+sGlobCT
		+"((.|\\n)*?)"
		+sGlobOT
		+"\\s*/\\s*"
		+sGlobTag
		+"\\s*"
		+sGlobCT;

	regExp=new RegExp(sRegExp, "gim");

	// render pre
	return sValue.replace( regExp,  function( $0, $1 ){ return replaceByCode( $0, $1 );} );
};

//////////////////////////////////////////////////////////////////////////////////////
// Initialization
/// <summary>Load language files and preprocess them. Loads xsl file.</summary>
function initHighlighting()
{
	var sXMLLang, sXSLFile;
	
	// getting xml and xsl file names
	sXMLLang = "highlight.xml";
	sXSLStyle = "highlight.xsl";
	
	// prepare tags
	sGlobOT = "<";
	sGlobCT = ">";
	sGlobTag = "pre";
	sGlobLangTag = "lang";	
	sGlobTemplate= "cpp";
	bGlobCodeInBox=true;

	try
	{	
		// load and preprocess language data
		xmlGlobDoc = loadAndBuildSyntax( loadXML( sXMLLang ) );
		// load xsl..
		xslGlobDoc = loadXML( sXSLStyle );
	}
	catch(exception)
	{
		handleException (exception);
		xmlGlobDoc = null;
		xslGlobDoc
		return false;
	}
	finally
	{
		return true;
	}
}

// Global variables...
var sGlobOT, sGlobTag, sGlobCT, sGlobLangTag, sGlobDefaultLang, bGlobCodeInBox;
var xmlGlobDoc, xslGlobDoc;

// Initialize and preparse...
initHighlighting();
