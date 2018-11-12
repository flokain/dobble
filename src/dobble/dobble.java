package dobble;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class dobble {

	public static void main(String[] args) throws Exception {

        System.out.println("Working Directory = " +
        System.getProperty("user.dir"));

	    if (args.length > 0) {	
			int	numSymbols = Integer.parseInt(args[1]); 			// numberSymbols Anzahl der dobble symbole
			int numCards = Integer.parseInt(args[2]);				// numberCards Anzahl der Karten
			int numSymbolsPerCard = Integer.parseInt(args[3]);		// Symbols Per Card Wieviele Symbole auf einer Karte
			int amountPerSymbol = Integer.parseInt(args[4]);
	    }
	    
		String path_Layout = "../dobble_vorlage.svg";   // .svg file which is used for creating cards
		
		/* create a Gui to select
			- path of the images
			- config file	
		*/		
		new Gui();
		
		//Calculate which symbol to display on what card.
		int[][] cards = calculateSymbols();
		
		int[][] randCards = randomize(cards);
		
		//draw all the Svg Cards.
		SvgReplicator.createSvgCards(cards);
		
		//edit
	}
	
	
	
	private static int[][] randomize(int[][]cards) {
		
		for (int i = 0 ; i < cards.length; i++){
			List<Integer> list = IntStream.of(cards[i]).boxed().collect(Collectors.toList());
			Collections.shuffle(list);
			cards[i] = list.stream().mapToInt(k->k).toArray();
		}
		
		for (int i = 0; i < cards.length; i++)
		{
			for (int j = 0; j < cards[0].length; j++)
			{
				System.out.print(cards[i][j] + ", ");
			}
			System.out.print("\n");
		}
		return cards;
	}


	public static int[][] calculateSymbols() {
		
	/**	in der endlichen Geometrie gilt die Regel f�r einen 2-dim Vektorraum:
	 *  "zwei Geraden schneiden sich genau einmal oder sind parallel",
	 *  genau dann wenn der k�rper �ber den Vektorraum eine Primzahl p ist.
	 *  Geraden haben dann genau p Elemente. Fasst man eine Dobblekarte als gerade 
	 *  auf, so hat diese p elemente. Allerdings schneiden sich parallele 
	 *  geraden nicht. deshalb geht	man zu projektiven geometrie �ber, 
	 *  wo man jeder Klasse von parallelen geraden einen eindeutigen 
	 *  "Fernpunkt" zu ordnet. �bersetzt hei�t dass das alle karten die
	 *  als Geraden parallel sind um ein weiteres gemeinsames Symbol erg�nzt werden.
	 *  
	 *  Das hei�t das die Anzahl von Symbolen pro Karte NICHT
	 *  frei w�hlbar ist, sondern von der form Primzahl + 1 ist.
	 *  wenn man die Geraden-eigenschaft verwenden will.
	 *  
	 * */
	
	int	numSymbols = 0; 			// numberSymbols Anzahl der dobble symbole
	int numCards = 0;				// numberCards Anzahl der Karten
	int numSymbolsPerCard = 8;		// Symbols Per Card Wieviele Symbole auf einer Karte
	int amountPerSymbol = 0;		// frequency of Symbols wieviele Karten haben das selbe symbol
		
		/* numSymbolsPerCard h�ngt mit numSymbols zusammen. Es berechnet sich aus der kleinsten PrimZahl f�r die gilt
		 * p^2 + p + 1 > numSymbols oder p > -1./2 + sqrt(1/4 + numSymbols - 1)
		 * Dann ist spC = p+1;
		 * 
		 * */
	int p;
	
	// checke welche parameter gegeben sind
	// legal ist nC, nC, spC, spC+fS
	
	if (numSymbols != 0)
	{
		p = nextPrime( (int)(Math.ceil(-1./2 + Math.sqrt(1/4 + numSymbols -1))));
		
		if (numCards > numSymbols || numCards == 0) 
			numCards = numSymbols;
	}
	else if (numSymbolsPerCard != 0) 
	{
		p = previousPrime(numSymbolsPerCard);
		numSymbols = p*p+p+1; //overwrite
			
		if (amountPerSymbol == 0) 
			amountPerSymbol = numSymbolsPerCard;
		
		numCards = (int) (numSymbols *amountPerSymbol /numSymbolsPerCard);
		// damit fS ann�hernd erf�llt wird, kann mann die Kartenzahl
		// anpassen. denn nC = nS *fS /spC abgerundet.
		// man l�scht einfach die letzten p karten aus dem Ergebnis.
	}
	else
		p = nextPrime( (int)(Math.ceil(-1./2 + Math.sqrt(1/4 + numSymbols -1))));
	
	int[][] lines = new int[p*p+p+1][p+1];
	
	for(int k = 0; k <= p ; k++)
	{
		for(int d = 0; p*p+p+1 - d+k*p > 0 && d<p ; d++)
		{
			// die symbole sind so im Vektorraum so codiert:
			//    |	Flucht      |0				| 1				|p-1	
			// ---+-------------+---------------+---------------+----
			//  0 |	0      		, 1    			, 2  	  		, p
			//  1 | p+1    		, p+1 +1		, p+1 +2  		, (p+1) +p = 3(p+1) -1
			//  2 | 2(p+1) 		, 2(p+1)+1		, 2(p+1)+2		, 2(p+1) +p 
			//.p-1| (p-1)(p+1) 	, (p-1)(p+1)+1	, (p-1)(p+1)+2	, (p-1)(p+1) +p = p^2 + p -1
			// p  | p*(p+1)=p^2 + p
			
			// die letzte Zeile fasst alle Geraden mit unendlicher Steigung zusammen.
			
			lines[d+k*p][0] = k*(p+1); // der Fluchtpunktindex ist gleich der Steigung f(k) = k(p+1);
			
			for(int x = 0; x < p ; x++)
			{
				//bestimme x und y Koordinaten einer geraden im gitter
				
				int xCoo = x;		   	
				int yCoo = (k*x +d)%p;
				
				if (k == p)		//Spezial fall uendliche steigung.
				{
					xCoo = d;
					yCoo = x;
				}
				
				
				// das symbol(x,y) = symbol( (y+1)(p+1) + x+1 );
				lines[d+k*p][x+1] = yCoo*(p+1) + xCoo+1;
			}
			// eine linie (d,k) besteht dann aus den punkten { f(k), s(0,d),s(1,d+x),...				
			}
	}
	// extremer Spezialfall: es werden genau cN = p^2+p+1 karten ben�tigt.
	// dann gibt es genau p+1 flucht punkte. die "Fluchtgerade" ist damit auch eine L�sung
	for(int k = 0; k <= p ; k++)
	{
		lines[numSymbols-1][k] = k*(p+1); // der Fluchtpunktindex ist gleich der Steigung f(k) = k(p+1);
	}
	
	int[][] results = new int[numCards][p+1];
	for (int i = 0; i < results.length; i++) 
	{
		results[i]=lines[i];
	}
	
	
	// print out the result
	
	for (int i = 0; i < results.length; i++)
	{
		for (int j = 0; j < results[0].length; j++)
		{
			System.out.print(results[i][j] + ", ");
		}
		System.out.print("\n");
	}
	return results;
	}
	
	private static int nextPrime( int n)
	{
		boolean primeNotFound = true;
		while(primeNotFound)
		{
			primeNotFound = false;
			for(int i=2;i<n;i++) 
			{
				if(n%i==0)
				{
					primeNotFound = true;
					n = n+1;
					break;
				}  
			}
		}
		return n;
	}
	private static int previousPrime( int n)
	{
		int j = n-1;
		
		for(int i=2;i < j;i++) 
		{
			if(j%i==0) //not a prime
			{
				j= j-1;
				i= 2;
			}
		}
		return j;
	}

}
