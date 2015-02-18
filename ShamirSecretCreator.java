import java.math.BigInteger;
import library.*;
import java.io.*;
import java.util.*;

class ShamirSecretCreator {
	public static void main(final String[] args) {

		BigInteger maxPrimeRange = new BigInteger("1"); /////////////////////////////////////////////////////  THIS NEEDS TO BE TAKEN CARE OF  ///////////////////////////////////////////////////////////
		int initTotalShares = 5;
		int initNeededShares = 3;
		String initDestination = "./shares/";
		String initInput = "input.txt";


		// handling the inputs given by the user
		if(args.length < 1){			
			System.out.println("Usage : java ShamirSecretCreator <input_file> {total_shares [default=11] } {needed_shares [default=10] } {destination_folder [default=./shares/] }");
			System.exit(0);
		}
		if(args.length > 0){
			initInput = args[0];
		}
		if(args.length > 1){
			initTotalShares = Integer.parseInt(args[1]);
		}
		if(args.length > 2){
			initNeededShares = Integer.parseInt(args[2]);
		}
		if(args.length > 3){
			initDestination = args[3];
		}

		// initializing everything
        int n = initTotalShares;
        int k = initNeededShares;
        final Shamir shamir = new Shamir(k,n);
        shamir.setPrime(maxPrimeRange);					
        final BigInteger prime = shamir.getPrime();

        // setting a dummy value in the input matrix
        BigInteger secretMatrix[][] = new BigInteger[1][1];secretMatrix[0][0] = new BigInteger("11");


        // reading the input matrix from the given input file and storing it into "secretMatrix"
        try{
		    BufferedReader fInput = new BufferedReader(new FileReader(initInput));
		    LineNumberReader lnRead = new LineNumberReader(new FileReader(initInput));
		    lnRead.skip(Long.MAX_VALUE);
		    secretMatrix = new BigInteger[lnRead.getLineNumber()+1][];
		    String line;
		    int i = 0;
			while((line = fInput.readLine()) != null){
				String lineWords[] = line.split(" ");
				secretMatrix[i] = new BigInteger[lineWords.length];
				for (int j=0;j<lineWords.length;j++) {
					secretMatrix[i][j] = new BigInteger(lineWords[j]);
				}
				i++;
			}
			fInput.close();
			lnRead.close();
		}catch(FileNotFoundException e){
			// thrown by file readers
			System.out.println("FileNotFoundException");
		}catch(IOException e){
			// thrown by bufferedreader.readline
			System.out.println("IOException");
		}        


		// splitting the data. A single digit returns a 1-D array , hence , a 2-D input will return a 3-D array
        final SecretShare[][][] sharesMatrix = shamir.splitMatrix2D(secretMatrix);

        try{
        	// create output directory if not exist
        	new File(initDestination).mkdirs();
        	
        	// store the primary info regarding the splitting into output-info.txt
        	BufferedWriter fSharesInfo = new BufferedWriter(new FileWriter(initDestination+"output-info.txt"));
        	fSharesInfo.write(prime+"");
        	fSharesInfo.newLine();
        	fSharesInfo.write(n+"");
        	fSharesInfo.newLine();
        	fSharesInfo.write(k+"");
        	fSharesInfo.newLine();
        	for(int x = 0 ; x < sharesMatrix[0][0].length ; x++){


        		String filename= "output-"+x+".txt";
        		BufferedWriter fShares = new BufferedWriter(new FileWriter(initDestination+filename));        		

	        	fSharesInfo.write(filename);
	        	fSharesInfo.newLine();

        		//fShares.write(sharesMatrix[0][0][x].getNum()+"");
        		//fShares.newLine();

        		for(int y = 0 ; y < sharesMatrix[0].length ; y++){
        			String writeThis = "";
        			for(int z = 0 ; z < sharesMatrix.length ; z++){
        				writeThis += sharesMatrix[z][y][x].getShare()+" ";
        			}
        			fShares.write(writeThis,0,writeThis.length()-1);
        			fShares.newLine();
        		}        		
				fShares.flush();
				fShares.close();
        			
        	}
        	fSharesInfo.close();
		}catch(FileNotFoundException e){
			// thrown by file readers
			System.out.println("FileNotFoundException");
		}catch(IOException e){
			// thrown by bufferedreader.readline
			System.out.println("IOException");
		}

		System.out.println("Your data is splitted successfully and the shares are created in : "+initDestination+" \n\nKindly see the output-info.txt file in destination for more info. \n\noutput-info.txt format : \n Line 1: Prime No. \n Line 2: n (Max Shares) \n Line 3: k (Min Needed Shares) \n Line 4 onwards: Output files of shares ");

    }
}