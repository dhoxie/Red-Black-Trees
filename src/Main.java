import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        RBT rbt = new RBT();
	    Scanner in = new Scanner(System.in);
	    rbt.console(in);
        in.close();
    }

}

class RBT{

    public void console(Scanner in){
        boolean done = false;
        while (!done && in.hasNextLine()){
            String line = in.nextLine();
            Scanner ls = new Scanner(line);
            String token = ls.next();
            Node node;
            switch (token.toUpperCase()){
                case "INSERT":
                    while (ls.hasNext()) {
                        node = new Node(ls.nextInt(), ls.next().charAt(0));
                        insert(node);
                    }
                    break;
                case "DELETE":
                    while (ls.hasNext()) {
                        node = new Node(ls.nextInt());
                        delete(node);
                    }
                    break;
                //recall: red value is 0, black value is 1
                //used 2 to indicate color does not matter
                case "PREORDER":
                    preorder(2);
                    break;
                case "INORDER":
                    inorder(2);
                    break;
                case "POSTORDER":
                    postorder(2);
                    break;
                case "INORDERRED":
                    inorder(0);
                    break;
                case "INORDERBLACK":
                    inorder(1);
                    break;
                case "PREORDERRED":
                    preorder(0);
                    break;
                case "PREORDERBLACK":
                    preorder(1);
                    break;
                case "POSTORDERRED":
                    postorder(0);
                    break;
                case "POSTORDERBLACK":
                    postorder(1);
                    break;
                case "EXIT":
                    ls.close();
                    done = true;
                    break;
            }
        }
    }

    private final int RED = 0;
    private final int BLACK = 1;

    private class Node{
        char data;
        int key, color = BLACK;
        Node l = nil, r = nil, p = nil;

        Node(int key){
            this.key = key;
        }
        Node(int key, char data){
            this.key = key;
            this.data = data;
        }
    }

    private final Node nil = new Node(-1);
    private Node root = nil;

    private Node findNode(Node findNode, Node n){
        if (root == nil) return null;
        if (findNode.key < n.key){
            if (n.l != nil) return findNode(findNode, n.l);
        } else if (findNode.key > n.key){
            if (n.r != nil) return findNode(findNode, n.r);
        } //now we know findNode.key == n.key
        return n;
    }

    private void insert(Node n){
        Node temp = root;
        if (root == nil){
            root = n;
            n.color = BLACK;
            n.p = nil;
        } else{
            n.color = RED;
            while (true){
                if (n.key < temp.key){
                    if (temp.l == nil){
                        temp.l = n;
                        n.p = temp;
                        break;
                    } else temp = temp.l;
                } else if (n.key >= temp.key){
                    if (temp.r == nil){
                        temp.r = n;
                        n.p = temp;
                        break;
                    } else temp = temp.r;
                }
            }
            fixTree(n);
        }
    }

    private void fixTree(Node n){
        while (n.p.color == RED){
            Node uncle = nil;
            if (n.p == n.p.p.l){
                uncle = n.p.p.r;
                if (uncle != nil && uncle.color == RED){
                    n.p.color = BLACK;
                    uncle.color = BLACK;
                    n.p.p.color = RED;
                    n = n.p.p;
                    continue;
                }
                if (n == n.p.r){
                    //Double rotation
                    n = n.p;
                    rotateLeft(n);
                }
                n.p.color = BLACK;
                n.p.p.color = RED;
                rotateRight(n.p.p);
            } else{
                uncle = n.p.p.l;
                if (uncle != nil && uncle.color == RED){
                    n.p.color = BLACK;
                    uncle.color = BLACK;
                    n.p.p.color = RED;
                    n = n.p.p;
                    continue;
                }
                if (n == n.p.l){
                    //double rotation
                    n = n.p;
                    rotateRight(n);
                }
                n.p.color = BLACK;
                n.p.p.color = RED;
                rotateLeft(n.p.p);
            }
        }
        root.color = BLACK;
    }

    void rotateLeft(Node n){
        if (n.p != nil){
            if (n == n.p.l) n.p.l = n.r;
            else n.p.r = n.r;
            n.r.p = n.p;
            n.p = n.r;
            if (n.r.l != nil) n.r.l.p = n;
            n.r = n.r.l;
            n.p.l = n;
        } else {
            Node right = root.r;
            root.r = right.l;
            right.l.p = root;
            root.p = right;
            right.l = root;
            right.p = nil;
            root = right;
        }
    }

    void rotateRight(Node n){
        if (n.p != nil){
            if (n == n.p.l) n.p.l = n.l;
            else n.p.r = n.l;
            n.l.p = n.p;
            n.p = n.l;
            if (n.l.r != nil) n.l.r.p = n;
            n.l = n.l.r;
            n.p.r = n;
        } else {
            Node left = root.l;
            root.l = root.l.r;
            left.r.p = root;
            root.p = left;
            left.r = root;
            left.p = nil;
            root = left;
        }
    }

    void transplant(Node target, Node with){
        if (target.p == nil) root = with;
        else if (target == target.p.l) target.p.l = with;
        else target.p.r = with;
        with.p = target.p;
    }

    void delete(Node z){
        if ((z = findNode(z, root)) == null) return;
        Node x;
        Node y = z;
        int y_orig_color = y.color;
        if (z.l == nil) {
            x = z.r;
            transplant(z, z.r);
        } else if (z.r == nil) {
            x = z.l;
            transplant(z, z.l);
        } else{
            y = treeMinimum(z.r);
            y_orig_color = y.color;
            x = y.r;
            if (y.p == z) x.p = y;
            else{
                transplant(y, y.r);
                y.r = z.r;
                y.r.p = y;
            }
            transplant(z, y);
            y.l =z.l;
            y.l.p = y;
            y.color = z.color;
        }
        if (y_orig_color == BLACK){
            deleteFixup(x);
        }
    }

    void deleteFixup(Node x){
        while (x != root && x.color == BLACK){
            if (x == x.p.l) {
                Node w = x.p.r;
                if (w.color == RED) {
                    w.color =BLACK;
                    x.p.color=RED;
                    rotateLeft(x.p);
                    w = x.p.r;
                }
                if (w.l.color == BLACK && w.r.color == BLACK){
                    w.color = RED;
                    x = x.p;
                    continue;
                } else if(w.r.color == BLACK){
                    w.l.color = BLACK;
                    w.color = RED;
                    rotateRight(w);
                    w = x.p.r;
                }
                if(w.r.color == RED){
                    w.color = x.p.color;
                    x.p.color = BLACK;
                    w.r.color = BLACK;
                    rotateLeft(x.p);
                    x = root;
                }
            }else{
                Node w = x.p.l;
                if (w.color == RED){
                    w.color = BLACK;
                    x.p.color = RED;
                    rotateRight(x.p);
                    w = x.p.l;
                }
                if (w.r.color == BLACK && w.l.color == BLACK) {
                    w.color = RED;
                    x = x.p;
                    continue;
                } else if(w.l.color == BLACK){
                    w.r.color = BLACK;
                    w.color = RED;
                    rotateLeft(w);
                    w = x.p.l;
                }
                if(w.l.color == RED){
                    w.color = x.p.color;
                    x.p.color = BLACK;
                    w.l.color = BLACK;
                    rotateRight(x.p);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }

    Node treeMinimum(Node subRoot){
        while (subRoot.l != nil) subRoot = subRoot.l;
        return subRoot;
    }

    void inorder(int c){
        inorder(root, c);
        System.out.println();
    }

    private void inorder(Node x, int c){
        if (x == nil) return;
        inorder(x.l, c);
        if (x.color == c || c == 2) System.out.print(x.data);
        inorder(x.r, c);
    }

    void preorder(int c){
        preorder(root, c);
        System.out.println();
    }

    private void preorder(Node x, int c){
        if (x == nil) return;
        if (x.color == c || c == 2) System.out.print(x.data);
        preorder(x.l, c);
        preorder(x.r, c);
    }

    void postorder(int c){
        postorder(root, c);
        System.out.println();
    }

    private void postorder(Node x, int c){
        if (x == nil) return;
        postorder(x.l, c);
        postorder(x.r, c);
        if (x.color == c || c == 2) System.out.print(x.data);
    }

}//end class RBT