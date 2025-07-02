package com.quickcolorswap;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int GetX(){ return x; }
    public int GetY(){ return y; }

    public void SetX(int x) { this.x = x; }
    public void SetY(int y) { this.y = y; }
    
    @Override
    public boolean equals(Object other){
        if(other == null){ return false; }
        if(other.getClass() != this.getClass()){return false;}
        Point new_other = (Point)other;
        if(new_other.GetX() == GetX() && new_other.GetY() == GetY()){
            return true;
        }
        return false;
    }
}
