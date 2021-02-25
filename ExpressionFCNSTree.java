/**************************************************************************
  * @author Niha Imam
  * CS310 Spring 2018
  * Project 3
  * George Mason University
  * 
  * File Name: ExpressionFCNSTree.java
  *
  * Description: used to represent expressions in FCNS-format
  ***************************************************************************/

import java.io.File;
import java.util.Scanner;
import java.io.FileReader; //used for buildTree
import java.io.BufferedReader; //used for buildTree
import java.io.FileNotFoundException;

public class ExpressionFCNSTree{
  
  //==========================
  // DO NOT CHANGE
  
  
  FCNSTreeNode root;
  public ExpressionFCNSTree(){
    root = null;
  }
  
  public ExpressionFCNSTree(FCNSTreeNode root){
    this.root = root;
  }
  
  public boolean equals(ExpressionFCNSTree another){
    return root.equals(another.root);
    
  }
  
  // END OF DO NOT CHANGE
  //==========================
  
  /**
   * calculate the size of the tree
   * @returns the size
   */
  public int size(){
    return calculateSize(root);
  }
  
  /**
   * helper method to calculate the size of the tree
   * @param the root
   * @returns the size
   */
  private int calculateSize(FCNSTreeNode node) {
    if (node == null) {
      return 0;
    }
    return 1 + calculateSize(node.firstChild) + calculateSize(node.nextSibling);
  }
  
  /**
   * calculate the height of the tree
   * @returns the height or -1 for empty tree
   */
  public int height() {
    return calculateHeight(root);
  }
  
  /**
   * helper method to calculate the height of the tree
   * @param the root
   * @returns the height
   */
  private int calculateHeight(FCNSTreeNode node) {
    if (node == null) {
      return -1;
    }
    return 1 + Math.max(calculateHeight(node.firstChild), calculateHeight(node.nextSibling));
  }
  
  /**
   * how many nodes have the same string
   * @param string that needs to be compared
   * @returns number of same nodes
   */
  public int countNode(String s){
    if (root == null)
      return 0;
    return countNodeHelp(root, s,1);
  }
  
  /**
   * count how many nodes are marked as not-a-number
   * @returns amount of nodes are marked as not-a-number
   */
  public int countNan(){
    if (root == null)
      return 0;
    return countNodeHelp(root, "",2);
  }
  
  /**
   * helper method for countNan and ountNode
   * @param string, node and mode
   * @returns number of countNan and ountNode
   */
  private int countNodeHelp(FCNSTreeNode node, String s, int mode){
    if (node == null)
      return 0;
    else {
      if (mode == 2 && node.nan == true)
      {
        return 1 + countNodeHelp(node.firstChild, s, mode) + countNodeHelp(node.nextSibling, s, mode);
      }
      else if (node.element.equals(s) == true && mode == 1)
        return 1 + countNodeHelp(node.firstChild, s, mode) + countNodeHelp(node.nextSibling, s, mode);
      else
        return countNodeHelp(node.firstChild, s, mode) + countNodeHelp(node.nextSibling, s, mode);
    }
  }
  
  /**
   * string representation of pre-order tree traversal
   * @returns a string
   */
  public String toStringPreFix(){
    return prefix(root);
  }
  
  /**
   * helper method to create a pre-order tree representation
   * @param root node as temp
   * @returns a string
   */
  private String prefix(FCNSTreeNode temp){
    String s = "";
    if (root == null)
      return " ";
    else {
      s += temp.element + " ";
      if (temp.firstChild != null)
        s = s + prefix(temp.firstChild) + " ";
      if (temp.nextSibling != null)
        s += prefix(temp.nextSibling);
      s = s.trim() + " ";
      return s;
    }
  }
  
  /**
   * string representation of post-order tree traversal
   * @returns a string
   */
  public String toStringPostFix(){
    if (root == null)
      return "";
    String n = postfix();
    String s = "";
    for (int i = 0; i < n.length(); i++){
      if (n.charAt(i) != ' ')
        s += n.charAt(i) + " ";
    }
    return s;
  }
  
  /**
   * helper method to create a post-order tree representation
   * @returns a string
   */
  private String postfix(){
    return (new ExpressionFCNSTree(root.firstChild).toStringPostFix()) + " " + 
      (new ExpressionFCNSTree(root.nextSibling).toStringPostFix()) + " " + root.element;
  }
  
  /**
   * string representation of level-order (breadth-first) tree traversal
   * @returns a string
   */
  public String toStringLevelOrder(){
    String s="";
    Queue<FCNSTreeNode> queue = new Queue<FCNSTreeNode>();
    queue.enqueue(root);
    while (!queue.isEmpty()) 
    {
      FCNSTreeNode tempNode = queue.dequeue();
      s += tempNode.element + " ";
      if (tempNode.firstChild != null){
        queue.enqueue(tempNode.firstChild);
      }
      if (tempNode.nextSibling != null){
        queue.enqueue(tempNode.nextSibling);
      }
    }
    return s;
  }  
  
  /**
   * build a tree by opening a file specified by the string fileName
   * read in a one-line numeric expression in prefix notation, and 
   * construct a first-child-next-sibling expression tree base on the input
   * if there is any exception, root should be null
   */
  public void buildTree(String fileName) throws FileNotFoundException{
    try {
      FileReader fr = new FileReader(fileName);
      BufferedReader bufferedReader = new BufferedReader(fr);
      String expre = null,t;
      while((t = bufferedReader.readLine()) != null) {
        expre = t;
      }
      String[] tokens= expre.split(" ");
      FCNSTreeNode temp = new FCNSTreeNode(tokens[tokens.length-1]);
      FCNSTreeNode curr = null;
      for(int i = tokens.length - 2; i >= 0; --i){
        if (!isOperator(temp.element) && isOperator(tokens[i])){
          curr = new FCNSTreeNode(tokens[i],temp,null);
        }
        else {
          curr = new FCNSTreeNode(tokens[i],null,temp);
        }
        temp=curr;
      }
      this.root = curr; 
      this.evaluate();
    }
    catch (Exception ex){
      root = null;
    }
  }
  
  /**
   * construct the binary tree representation of this expression 
   * @returns the root of the binary tree created
   */
  public ExpressionBinaryTree buildBinaryTree(){
    if (root == null)
      return new ExpressionBinaryTree();
    if (root.firstChild == null)
      return new ExpressionBinaryTree(new BinaryTreeNode(root.element));
    BinaryTreeNode bleft = new ExpressionFCNSTree(root.firstChild).buildBinaryTree().root;
    BinaryTreeNode bright = new ExpressionFCNSTree(root.firstChild.nextSibling).buildBinaryTree().root;
    BinaryTreeNode broot = new BinaryTreeNode(root.element, bleft, bright);
    return new ExpressionBinaryTree(broot);
  }
  
  /**
   * create a string representation as the normal human-friendly infix expression
   * @returns a string
   */
  public String toStringPrettyInFix(){
    Stack<String> st = new Stack<String>();
    String pr = toStringPreFix().trim();
    String[] pre_exp = pr.split(" ");
    int length = pre_exp.length;
    for (int i = length - 1; i >= 0; i--){
      if (isOperator(pre_exp[i])){
        String op2="";
        String op1 = st.pop();
        if(!"~".equals(pre_exp[i]))
          op2= st.pop();
        String temp="";
        if("~".equals(pre_exp[i]))
          temp=pre_exp+op1;
        temp = "(" + op1 + pre_exp[i] + op2 + ")";
        st.push(temp);
      }
      else {
        st.push(pre_exp[i]);
      }
    }
    return st.peek();
  }
  
  /**
   * helper method to check if string is an operator
   * @returns true if it is else false
   */
  private boolean isOperator(String s){
    try {
      int t = Integer.parseInt(s);
      return false;
    }
    catch(Exception e) {
      return true;
    }
  }
  
  /**
   * evaluates the expression and marks every tree node with
   * operand node: node.value should be the integer value of the operand
   * operator node: node.value should be the integer value associated with the sub-expression
   * @returns the integer value of the root node or null for null tree
   */
  public Integer evaluate(){
    if (root == null){
      return null;
    }
    return getValue(root);
  }
  
  /**
   * helper method for evaluate
   * @returns the integer value of the root node
   */
  private Integer getValue(FCNSTreeNode node){
    if (node.element.equals("+")){
      node.value = getValue(node.firstChild) + getValue(node.firstChild.nextSibling);
      node.nan = false;
    }
    else if (node.element.equals("-")){
      node.value = getValue(node.firstChild) - getValue(node.firstChild.nextSibling);
      node.nan = false;
    }
    else if (node.element.equals("*")){
      node.nan = false;
      node.value = getValue(node.firstChild) * getValue(node.firstChild.nextSibling);
    }
    else if (node.element.equals("/")){
      if (getValue(node.firstChild.nextSibling) == 0){
        node.nan = true;
        node.value = null;
      }
      else {
        node.nan = false;
        node.value = getValue(node.firstChild) / getValue(node.firstChild.nextSibling);
      }
    } 
    else if (node.element.equals("%")){
      node.nan = false;
      node.value = getValue(node.firstChild) % getValue(node.firstChild.nextSibling);
    }
    else {
      node.value = Integer.parseInt(node.element);
    }
    return node.value;
  }
  
  /**
   * evaluates the expression leaving the tree unchanged 
   * does the same as evaluate() except its not recursive
   * @returns the integer value of the root node or null for null tree
   */
  public Integer evaluateNonRec(){
    if (root == null)
      return null;
    else {
      Stack<String> op = new Stack<String>();
      Stack<Integer> val = new Stack<Integer>();
      String [] token = this.toStringPostFix().split(" ");
      int i;
      for (i = 0; i < token.length; ++i){
        if (!isOperator(token[i]))
          val.push(Integer.parseInt(token[i]));
        else {
          int val2 = 0;
          int val1 = val.pop();
          if (!token[i].equals("~"))
            val2 = val.pop();
          switch(token[i])
          {
            case "+": val.push(val2+val1); break;
            case "-": val.push(val1-val2); break;
            case "*": val.push(val2*val1); break;
            case "/": 
              if (val2 == 0 || val1 == 0)
              val.push(0);
              else
                val.push(val2/val1);
              break;
            case "%": 
              if (val2 == 0 || val1 == 0)
              val.push(0);
              else
                val.push(val2%val1);  
              break;    
            case "~": val.push(val1*(-1));  break;
          }
        }
      }
      return val.pop();
    }
  }
  
  
  //----------------------------------------------------
  // example testing code... make sure you pass all ...
  // and edit this as much as you want!
  
  public static void main(String[] args) throws FileNotFoundException{
    
    //     *     *
    //   /  \       /
    //  /    \                1
    //  1     +   ==>    \
    //       / \                +
    //      2   3      /
    //                        2
    //         \
    //                          3
    //
    // prefix: * 1 + 2 3 (expr1.txt)
    
    FCNSTreeNode n1 = new FCNSTreeNode("3");
    FCNSTreeNode n2 = new FCNSTreeNode("2",null,n1);
    FCNSTreeNode n3 = new FCNSTreeNode("+",n2,null);
    FCNSTreeNode n4 = new FCNSTreeNode("1",null,n3);
    FCNSTreeNode n5 = new FCNSTreeNode("*",n4,null);
    ExpressionFCNSTree etree = new ExpressionFCNSTree(n5);
    
    if (etree.size()==5)
      System.out.println("size works");
    if (etree.height()==4) 
      System.out.println("height works");
    if (etree.countNan()==0)
      System.out.println("countNan works");
    if (etree.countNode("+") == 1)
      System.out.println("countNode works");
    
    if (etree.toStringPreFix().equals("* 1 + 2 3 "))
      System.out.println("pre fix works");
    if (etree.toStringPrettyInFix().equals("(1*(2+3))"))
      System.out.println("in fix works");
    if (etree.toStringPostFix().equals("3 2 + 1 * "))
      System.out.println("post fix works");
    if (etree.toStringLevelOrder().equals("* 1 + 2 3 "))
      System.out.println("level order works");
    
    if (etree.evaluateNonRec() == 5)
      System.out.println("evaluate non recursive works");
    
    if (etree.evaluate() == 5)
      System.out.println("evaluate works");
    if (n4.value == 1)
      System.out.println("part a");
    if (n3.value == 5)
      System.out.println("part b");
    if (!n5.nan)
      System.out.println("part c");
    
    ExpressionFCNSTree etree2 = new ExpressionFCNSTree();
    etree2.buildTree("expressions/expr1.txt"); // construct expression tree from pre-fix notation
    
    if (etree2.equals(etree)){
      System.out.println("build tree works");
    }
    
    BinaryTreeNode bn1 = new BinaryTreeNode("1");
    BinaryTreeNode bn2 = new BinaryTreeNode("2");
    BinaryTreeNode bn3 = new BinaryTreeNode("3");
    BinaryTreeNode bn4 = new BinaryTreeNode("+",bn2,bn3);
    BinaryTreeNode bn5 = new BinaryTreeNode("*",bn1,bn4);
    ExpressionBinaryTree btree = new ExpressionBinaryTree(bn5);
    
    //construct binary tree from first-child-next-sibling tree
    ExpressionBinaryTree btree2 = etree.buildBinaryTree(); 
    if (btree2.equals(btree)){
      System.out.println("build binary tree works");
    }
    
    ExpressionFCNSTree etree3 = new ExpressionFCNSTree();
    etree3.buildTree("expressions/expr5.txt"); // an example of an expression with division-by-zero
    if (etree3.evaluate() == null)
      System.out.println("evaluate works");
    if (etree3.countNan() == 1)
      System.out.println("countNan works");
    
  }
}


//=======================================
// Tree node class implemented for you
// DO NOT CHANGE
class FCNSTreeNode{
  
  //members
  String element; //symbol represented by the node, can be either operator or operand (integer)
  Boolean nan; //boolean flag, set to be true if the expression is not-a-number
  Integer value;  //integer value associated with the node, used in evaluation
  FCNSTreeNode firstChild;
  FCNSTreeNode nextSibling;
  
  //constructors
  public FCNSTreeNode(String el){
    element = el;
    nan = false;
    value = null;
    firstChild = null;
    nextSibling = null;
  }
  
  //constructors
  public FCNSTreeNode(String el,FCNSTreeNode fc, FCNSTreeNode ns ){
    element = el;
    nan = false;
    value = null;
    firstChild = fc;
    nextSibling = ns;
  }
  
  
  // toString
  @Override 
  public String toString(){
    return element.toString();
  }
  
  // compare two nodes 
  // return true if: 1) they have the same element; and
  //                 2) their have matching firstChild (subtree) and nextSibling (subtree)
  public boolean equals(FCNSTreeNode another){
    if (another==null)
      return false;
    
    if (!this.element.equals(another.element))
      return false;
    
    if (this.firstChild==null){
      if (another.firstChild!=null)
        return false;
    }
    else if (!this.firstChild.equals(another.firstChild))
      return false;
    
    if (this.nextSibling==null){
      if (another.nextSibling!=null)
        return false;
    }
    else if (!this.nextSibling.equals(another.nextSibling))
      return false;
    
    return true;
    
  }
  
}