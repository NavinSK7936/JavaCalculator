import user.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Calculator extends JFrame implements ActionListener {
  
    JTextField l;
  
    String s, box;
    
    @SuppressWarnings("unchecked")
    DLList<String>._Node<String> curr;
    DLList<String> cache;
    
    static final ArrayList<String> functions = new ArrayList<String>() {{
	add("ln");	add("log");	add("sin");	add("cos");	add("tan");	add("sqrt");}};
    
    static final String names[] = {				       " <- ",  "  C ",
			  " sin",  " cos",  " tan",  "  7 ",  "  8 ",  "  9 ",  "  + ",
			  " 1/x",  " log",  " ln ",  "  4 ",  "  5 ",  "  6 ",  "  - ",
			  "^",     "10^x",  "e^x",   "  1 ",  "  2 ",  "  3 ",  "  *" ,
			  "sqrt",  " pi ",  "  e ",  "  . ",  "  0 ",  "  = ",  "  / ",
					    " <= ",  "  ( ",  "  ) ",  " => "		};
    
    Calculator(String title) {
	this.l = new JTextField(32);
	this.l.setEditable(false);

	JPanel p = new JPanel();
	p.setBackground(Color.blue); 
	p.add(this.l);
	
	JButton buttons[] = new JButton[34];

	for(int i = 0; i < 34; i++) {
		buttons[i] = new JButton(names[i]);
		buttons[i].addActionListener(this);
		p.add(buttons[i]);}
	
	this.curr = (this.cache = new DLList<String>(box = "")).tail;
	
        this.add(p);
	this.setTitle(title);
	this.setVisible(true);
	this.setResizable(false);
        this.setBounds(700, 350, 401, 220);
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.BLACK));}
    
    public static void main(String args[]) {
	try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
	catch(Exception e) {System.err.println(e.getMessage());}
	new Calculator("Calculator");}
    
    @Override public void actionPerformed(ActionEvent e) {
	s = e.getActionCommand().trim();
	
	if(s.charAt(s.length()-1) == 'x')
		s = s.substring(0, s.length()-1);
	
	if(s.equals("<=")) {
		if(curr.prev != null)
			box = (curr = curr.prev).val;}
	else if(s.equals("=>")) {
		if(curr.next != null)
			box = (curr = curr.next).val;}
	else {
		if(s.equals("C"))
			box = "";
		else if(s.equals("<-"))
			box = box.substring(0, box.length()-1);
		else if(s.equals("="))
			cache.append(box = eval(box));
  		else
			box += s;
		curr = cache.tail;}
	
	if(functions.contains(s) || s.charAt(s.length()-1) == '^')
		box += "(";
	
	System.out.println(box);
	l.setText(box);
	
	if(box.equals("ERROR"))
		box = "";}
    
    public static String eval(String str) {
	
	System.out.print(str + " = ");
	str = "(" + str +")";
	
	char c;
	Stack<String> funcs = new Stack<String>();
    	Stack<Double> values = new Stack<Double>();
	Stack<Character> ops = new Stack<Character>();
	
	try {
		for(int i = 0; i < str.length(); i++)
			if((c = str.charAt(i)) >= '0' && c <= '9' || c == '.') {
				String aid = "";
				while(str.charAt(i) >= '0' && str.charAt(i) <= '9' || str.charAt(i) == '.')
					aid += str.substring(i, ++i);
				i--;
				values.push(Double.parseDouble(aid));}
			else if(c == 'e')
				values.push(Math.E);
			else if(c == 'E') {
				ops.push('*');	ops.push('^');
				values.push(10.0);}
			else if(c == '(') {
				funcs.push("");
				ops.push('(');
				if(str.charAt(i+1) == '-')	values.push(0.0);}
			else if(c == ')') {
				while(ops.peek() != '(')
					values.push(operate(ops.pop(), values.pop(), values.pop()));
				ops.pop();
				
				String func = funcs.pop();
				if(func.equals("ln"))		values.push(Math.log(values.pop()));
				else if(func.equals("log"))	values.push(Math.log10(values.pop()));
				else if(func.equals("sin"))	values.push(Math.sin(values.pop()));
				else if(func.equals("cos"))	values.push(Math.cos(values.pop()));
				else if(func.equals("tan"))	values.push(Math.tan(values.pop()));
				else if(func.equals("sqrt"))	values.push(Math.sqrt(values.pop()));}
			else if(c == '^' || c == '*' || c == '/' || c == '+' || c == '-') {
				while(precedence(c) <= precedence(ops.peek()))
					values.push(operate(ops.pop(), values.pop(), values.pop()));
				ops.push(c);}
			else if(str.substring(i, i+2).equals("pi")) {
				values.push(Math.PI);
				i++;}
			else if(str.substring(i, i+2).equals("ln")) {
				funcs.push("ln");
				ops.push('(');
				i += 2;
				if(str.charAt(i+1) == '-')	values.push(0.0);}
			else if(functions.contains(str.substring(i, i+3))) {
				funcs.push(str.substring(i, i+3));
				ops.push('(');
				i += 3;
				if(str.charAt(i+1) == '-')	values.push(0.0);}
			else if(str.substring(i, i+4).equals("sqrt")) {
				funcs.push("sqrt");
				ops.push('(');
				i += 4;
				if(str.charAt(i+1) == '-')	values.push(0.0);}}
	catch(Exception e) {
		return "ERROR";}
	return String.valueOf(values.pop());}
	
	public static Double operate(char op, Double a, Double b){
	    	switch(op) {
	       		case '+': return b + a;
		       	case '-': return b - a;
		        case '*': return b * a;
	       		case '/': return b / a;
		}return Math.pow(b, a);}
	
	public static int precedence(char op) {
		return op == '^' ? 3 : op == '*' ? 2 : op == '/' ? 2 : op == '+' ? 1 : op == '-' ? 1 : 0;}
}