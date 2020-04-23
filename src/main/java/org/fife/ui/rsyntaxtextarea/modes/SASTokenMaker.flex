/*
 * 02/25/2005
 *
 * SASTokenMaker.java - Scanner for SAS files.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.ui.rsyntaxtextarea.modes;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


/**
 * This class generates tokens representing a text stream as SAS.<p>
 *
 * This implementation was created using
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.1; however, the generated file
 * was modified for performance.  Memory allocation needs to be almost
 * completely removed to be competitive with the handwritten lexers (subclasses
 * of <code>AbstractTokenMaker</code>, so this class has been modified so that
 * Strings are never allocated (via yytext()), and the scanner never has to
 * worry about refilling its buffer (needlessly copying chars around).
 * We can achieve this because RText always scans exactly 1 line of tokens at a
 * time, and hands the scanner this line as an array of characters (a Segment
 * really).  Since tokens contain pointers to char arrays instead of Strings
 * holding their contents, there is no need for allocating new memory for
 * Strings.<p>
 *
 * The actual algorithm generated for scanning has, of course, not been
 * modified.<p>
 *
 * If you wish to regenerate this file yourself, keep in mind the following:
 * <ul>
 *   <li>The generated SASTokenMaker.java</code> file will contain two
 *       definitions of both <code>zzRefill</code> and <code>yyreset</code>.
 *       You should hand-delete the second of each definition (the ones
 *       generated by the lexer), as these generated methods modify the input
 *       buffer, which we'll never have to do.</li>
 *   <li>You should also change the declaration/definition of zzBuffer to NOT
 *       be initialized.  This is a needless memory allocation for us since we
 *       will be pointing the array somewhere else anyway.</li>
 *   <li>You should NOT call <code>yylex()</code> on the generated scanner
 *       directly; rather, you should use <code>getTokenList</code> as you would
 *       with any other <code>TokenMaker</code> instance.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 0.5
 *
 */
%%

%public
%class SASTokenMaker
%extends AbstractJFlexTokenMaker
%unicode
%ignorecase
%type org.fife.ui.rsyntaxtextarea.Token


%{


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public SASTokenMaker() {
		super();
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int tokenType) {
		addToken(zzStartRead, zzMarkedPos-1, tokenType);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *                    occurs.
	 */
	public void addToken(char[] array, int start, int end, int tokenType, int startOffset) {
		super.addToken(array, start,end, tokenType, startOffset);
		zzStartRead = zzMarkedPos;
	}


	/**
	 * Returns the text to place at the beginning and end of a
	 * line to "comment" it in a this programming language.
	 *
	 * @return The start and end strings to add to a line to "comment"
	 *         it out.
	 */
	public String[] getLineCommentStartAndEnd() {
		return new String[] { "*", null };
	}


	/**
	 * Returns whether tokens of the specified type should have "mark
	 * occurrences" enabled for the current programming language.
	 *
	 * @param type The token type.
	 * @return Whether tokens of this type should have "mark occurrences"
	 *         enabled.
	 */
	public boolean getMarkOccurrencesOfTokenType(int type) {
		return type==Token.IDENTIFIER || type==Token.VARIABLE;
	}


	/**
	 * Returns the first token in the linked list of tokens generated
	 * from <code>text</code>.  This method must be implemented by
	 * subclasses so they can correctly implement syntax highlighting.
	 *
	 * @param text The text from which to get tokens.
	 * @param initialTokenType The token type we should start with.
	 * @param startOffset The offset into the document at which
	 *        <code>text</code> starts.
	 * @return The first <code>Token</code> in a linked list representing
	 *         the syntax highlighted text.
	 */
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

		resetTokenList();
		this.offsetShift = -text.offset + startOffset;

		// Start off in the proper state.
		int state = Token.NULL;
		switch (initialTokenType) {
			case Token.LITERAL_STRING_DOUBLE_QUOTE:
				state = STRING;
				start = text.offset;
				break;
			case Token.LITERAL_CHAR:
				state = CHAR;
				start = text.offset;
				break;
			case Token.COMMENT_MULTILINE:
				state = MLC;
				start = text.offset;
				break;
			default:
				state = Token.NULL;
		}

		s = text;
		try {
			yyreset(zzReader);
			yybegin(state);
			return yylex();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new Token();
		}

	}


	/**
	 * Refills the input buffer.
	 *
	 * @return      <code>true</code> if EOF was reached, otherwise
	 *              <code>false</code>.
	 * @exception   IOException  if any I/O-Error occurs.
	 */
	private boolean zzRefill() throws java.io.IOException {
		return zzCurrentPos>=s.offset+s.count;
	}


	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>YY_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
	public final void yyreset(java.io.Reader reader) throws java.io.IOException {
		// 's' has been updated.
		zzBuffer = s.array;
		/*
		 * We replaced the line below with the two below it because zzRefill
		 * no longer "refills" the buffer (since the way we do it, it's always
		 * "full" the first time through, since it points to the segment's
		 * array).  So, we assign zzEndRead here.
		 */
		//zzStartRead = zzEndRead = s.offset;
		zzStartRead = s.offset;
		zzEndRead = zzStartRead + s.count - 1;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
		zzLexicalState = YYINITIAL;
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
	}


%}

LineTerminator		= ([\n])
Letter			= ([A-Za-z_])
Digit			= ([0-9])
Whitespace		= ([ \t]+)
Semicolon			= ([;])

Identifier		= (({Letter}|{Digit})+)
MacroVariable		= (&{Identifier})

Operators1		= ("+"|"-"|"*"|"/"|"^"|"|")
Operators2		= (([\^\~]?=)|(">"[=]?)|("<"[=]?))
Operators3		= ("eq"|"ne"|"gt"|"lt"|"ge"|"le"|"in")
Operator			= ({Operators1}|{Operators2}|{Operators3})
Separator			= ([\(\)])

StringBoundary		= (\")
CharBoundary		= (\')

LineCommentBegin	= ("*")
MLCBegin			= ("/*")
MLCEnd			= ("*/")

%state STRING
%state CHAR
%state MLC

%%

<YYINITIAL>  {

	/* Keywords */
	"_all_" |
	"_character_" |
	"_data_" |
	"_infile_" |
	"_last_" |
	"_null_" |
	"_numeric_" |
	"_page_" |
	"_temporary_" |
	"abend" |
	"abort" |
	"all" |
	"alter" |
	"and" |
	"array" |
	"as" |
	"ascending" |
	"attrib" |
	"axis" |
	"bell" |
	"blank" |
	"border" |
	"bounds" |
	"by" |
	"call" |
	"cancel" |
	"cards" |
	"cards4" |
	"choro" |
	"class" |
	"classes" |
	"clear" |
	"close" |
	"compute" |
	"contrast" |
	"coord" |
	"coordinates" |
	"cov" |
	"create" |
	"data" |
	"datalines" |
	"datalines4" |
	"delete" |
	"descending" |
	"describe" |
	"discrete" |
	"disk" |
	"display" |
	"dm" |
	"do" |
	"drop" |
	"dummy" |
	"else" |
	"end" |
	"endrsubmit" |
	"endsas" |
	"error" |
	"except" |
	"expandtabs" |
	"factors" |
	"file" |
	"filename" |
	"flowover" |
	"footnote" |
	"frame" |
	"freq" |
	"from" |
	"go" |
	"goption" |
	"goptions" |
	"goto" |
	"grid" |
	"group" |
	"groupby" |
	"groupformat" |
	"having" |
	"haxis" |
	"hbar" |
	"heading" |
	"high" |
	"html" |
	"id" |
	"if" |
	"infile" |
	"informat" |
	"inner" |
	"input" |
	"insert" |
	"intersect" |
	"keep" |
	"keylabel" |
	"label" |
	"lable" |
	"legend" |
	"length" |
	"libname" |
	"lineqs" |
	"link" |
	"list" |
	"listing" |
	"log" |
	"lostcard" |
	"low" |
	"mark" |
	"matings" |
	"mean" |
	"merge" |
	"missing" |
	"missover" |
	"mod" |
	"model" |
	"modify" |
	"n" |
	"nocell" |
	"nocharacters" |
	"nodupkey" |
	"noexpandtabs" |
	"noframe" |
	"noheading" |
	"noinput" |
	"nolegend" |
	"nopad" |
	"noprint" |
	"nosharebuffers" |
	"not" |
	"note" |
	"notitle" |
	"notitles" |
	"notsorted" |
	"ods" |
	"old" |
	"option" |
	"or" |
	"order" |
	"orderby" |
	"other" |
	"otherwise" |
	"outer" |
	"output" |
	"over" |
	"overlay" |
	"overprint" |
	"pad" |
	"pageby" |
	"pagesize" |
	"parmcards" |
	"parmcards4" |
	"parms" |
	"pattern" |
	"pct" |
	"pctn" |
	"pctsum" |
	"picture" |
	"pie" |
	"pie3d" |
	"plotter" |
	"predict" |
	"prefix" |
	"printer" |
	"proc" |
	"ps" |
	"put" |
	"quit" |
	"random" |
	"range" |
	"remove" |
	"rename" |
	"response" |
	"replace" |
	"reset" |
	"retain" |
	"return" |
	"rsubmit" |
	"run" |
	"s2" |
	"select" |
	"set" |
	"sharebuffers" |
	"signoff" |
	"signon" |
	"sim" |
	"skip" |
	"source2" |
	"startsas" |
	"std" |
	"stop" |
	"stopover" |
	"strata" |
	"sum" |
	"sumby" |
	"supvar" |
	"symbol" |
	"table" |
	"tables" |
	"tape" |
	"terminal" |
	"test" |
	"then" |
	"time" |
	"title" |
	"to" |
	"transform" |
	"treatments" |
	"truncover" |
	"unbuf" |
	"unbuffered" |
	"union" |
	"until" |
	"update" |
	"validate" |
	"value" |
	"var" |
	"variables" |
	"vaxis" |
	"vbar" |
	"weight" |
	"when" |
	"where" |
	"while" |
	"with" |
	"window" |
	"x"				{ addToken(Token.RESERVED_WORD); }

	/* Base SAS procs. */
	"append" |
	"calendar" |
	"catalog" |
	"chart" |
	"cimport" |
	"compare" |
	"contents" |
	"copy" |
	"cpm" |
	"cport" |
	"datasets" |
	"display" |
	"explode" |
	"export" |
	"fontreg" |
	"format" |
	"forms" |
	"fslist" |
	"import" |
	"means" |
	"migrate" |
	"options" |
	"optload" |
	"optsave" |
	"plot" |
	"pmenu" |
	"print" |
	"printto" |
	"proto" |
	"prtdef" |
	"prtexp" |
	"pwencode" |
	"rank" |
	"registry" |
	"report" |
	"sort" |
	"sql" |
	"standard" |
	"summary" |
	"tabulate" |
	"template" |
	"timeplot" |
	"transpose"			{ addToken(Token.DATA_TYPE); }

	/* SAS/STAT procs. */
	"corr" |
	"freq" |
	"univariate"			{ addToken(Token.DATA_TYPE); }

	/* Macros. */
	"%abort" |
	"%bquote" |
	"%by" |
	"%cms" |
	"%copy" |
	"%display" |
	"%do" |
	"%else" |
	"%end" |
	"%eval" |
	"%global" |
	"%go" |
	"%goto" |
	"%if" |
	"%inc" |
	"%include" |
	"%index" |
	"%input" |
	"%keydef" |
	"%length" |
	"%let" |
	"%local" |
	"%macro" |
	"%mend" |
	"%nrbquote" |
	"%nrquote" |
	"%nrstr" |
	"%put" |
	"%qscan" |
	"%qsubstr" |
	"%qsysfunc" |
	"%quote" |
	"%qupcase" |
	"%scan" |
	"%str" |
	"%substr" |
	"%superq" |
	"%syscall" |
	"%sysevalf" |
	"%sysexec" |
	"%sysfunc" |
	"%sysget" |
	"%sysprod" |
	"%sysrput" |
	"%then" |
	"%to" |
	"%tso" |
	"%unquote" |
	"%until" |
	"%upcase" |
	"%while" |
	"%window"				{ addToken(Token.FUNCTION); }

}

<YYINITIAL> {

	{LineTerminator}				{ addNullToken(); return firstToken; }

	/* Comments. */
	/* Do comments before operators as "*" can signify a line comment as */
	/* well as an operator. */
	^[ \t]*{LineCommentBegin}		{
									// We must do this because of how we
									// abuse JFlex; since we return an entire
									// list of tokens at once instead of a
									// single token at a time, the "^" regex
									// character doesn't really work, so we must
									// check that we're at the beginning of a
									// line ourselves.
									start = zzStartRead;
									// Might not be any whitespace.
									if (yylength()>1) {
										addToken(zzStartRead,zzMarkedPos-2, Token.WHITESPACE);
										zzStartRead = zzMarkedPos-1;
									}
									// Remember:  zzStartRead may now be updated,
									// so we must check against 'start'.
									if (start==s.offset) {
										addToken(zzStartRead,zzEndRead, Token.COMMENT_EOL);
										addNullToken();
										return firstToken;
									}
									else {
										addToken(zzStartRead,zzStartRead, Token.OPERATOR);
									}
								}
	{MLCBegin}					{ start = zzMarkedPos-2; yybegin(MLC); }

	/* Do operators before identifiers since some of them are words. */
	{Operator}					{ addToken(Token.OPERATOR); }
	{Separator}					{ addToken(Token.SEPARATOR); }

	{Identifier}					{ addToken(Token.IDENTIFIER); }
	{MacroVariable}				{ addToken(Token.VARIABLE); }
	{Semicolon}					{ addToken(Token.IDENTIFIER); }

	{Whitespace}					{ addToken(Token.WHITESPACE); }

	{StringBoundary}				{ start = zzMarkedPos-1; yybegin(STRING); }
	{CharBoundary}					{ start = zzMarkedPos-1; yybegin(CHAR); }

	<<EOF>>						{ addNullToken(); return firstToken; }

	/* Catch any other (unhandled) characters and flag them as OK;    */
	/* This will include "." from statements like "from lib.dataset". */
	.							{ addToken(Token.IDENTIFIER); }

}

<STRING> {

	[^\n\"]+						{}
	{LineTerminator}				{ addToken(start,zzStartRead-1, Token.LITERAL_STRING_DOUBLE_QUOTE); return firstToken; }
/*	{StringBoundary}{StringBoundary}	{} */
	{StringBoundary}				{ yybegin(YYINITIAL); addToken(start,zzStartRead, Token.LITERAL_STRING_DOUBLE_QUOTE); }
	<<EOF>>						{ addToken(start,zzStartRead-1, Token.LITERAL_STRING_DOUBLE_QUOTE); return firstToken; }

}

<CHAR> {

	[^\n\']+						{}
	{LineTerminator}				{ yybegin(YYINITIAL); addToken(start,zzStartRead-1, Token.LITERAL_CHAR); return firstToken; }
/*	{CharBoundary}{CharBoundary}		{} */
	{CharBoundary}					{ yybegin(YYINITIAL); addToken(start,zzStartRead, Token.LITERAL_CHAR); }
	<<EOF>>						{ addToken(start,zzStartRead-1, Token.LITERAL_CHAR); return firstToken; }

}

<MLC> {

	[^\n\*]+						{}
	{LineTerminator}				{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }
	{MLCEnd}						{ yybegin(YYINITIAL); addToken(start,zzStartRead+1, Token.COMMENT_MULTILINE); }
	\*							{}
	<<EOF>>						{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }

}
