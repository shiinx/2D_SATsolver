# 50.002 Computation Structures component for 2D hell week
<sub><sup>This architecture largely ignores the competition due to multiple mid-terms on-going during the 2D week. Look elsewhere for best build!</sup></sub>  
<sub><sup>Architecture used is, below 3ns and easy to do and rather huge</sup></sub>  
<sub><sup>To obtain maximum score in the Computation Structure component, an adder architecture below 3ns is required.</sup></sub>  
  
## Hybrid combination of Carry Look-Ahead (Kogge-Stone) and Carry select.  
  
Uses a Hybrid architecture to make life easier.  
<sub><sup>(In regards to jsim and circuit complexity.</sup></sub>  
  
## Circuit diagram  
![Image of circuit](/50.002_ComStruct/Circuit.jpg)  

* **Each KSA is 8-bit**  
  Takes in 8-bits of A, 8-bits of B and outputs 8-bits of SUM  
* Depending on carry output from the first Carry out, **C8**, the corresponding appropriate **s[8] to s[31]** will be selected from the other KSA outputs  
  The 6 KSA on the right will produce sum result depending on the A and B input.  
  Carry can only be either 0 or 1. So results for both possibilities are generated.  
  The mux will select the appropriate S to output depending on the carry.  
  
## .BC  
Different output bits were chosen for testing  
**s7.bc** : Compare S[7] bit of **Cascade Ripple Adder** if equal to S[7] of **Kogge-Stone Adder**  
**s12.bc** : Compare S[12] bit of **Cascade Ripple Adder** if equal to S[12] of **Hybrid Kogge-Stone w/ Carry Select Adder** (Using PG method)  
**s15.bc** : Compare S[12] bit of **Cascade Ripple Adder** if equal to S[12] of **Hybrid Kogge-Stone w/ Carry Select Adder** (Using Boolean Algebra Expression)  

s7 checks for KSA while s12 and s15 checks both KSA and selector  

## .cnf  
Conversion of .bc to .cnf using the bctocnf converter  
