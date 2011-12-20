#include <stdio.h>
#include <time.h>
#include <gmp.h>

void main()
{
	
	mpz_t A;
	mpz_t B;
	mpz_t C;
	FILE * bigA = NULL;
	FILE * bigB = NULL;
	int LENGTH = 301033;
	char aArr[LENGTH];
	char bArr[LENGTH];
	char *aName = "/home/ilgo/Code/workspace/zen.ilgo.tools/test/zen/ilgo/pipeline/gcdTest/BigIntegers/biggies%02d";
	char name[100];

	int c = 1;

	while (c <= 20)
	{
		sprintf(name, aName, c++);
		if(!(bigA = fopen(name, "r")))
		{
	  		printf("Error opening %s for writing. Program terminated.", aName);
	  		exit(1);
		}
		sprintf(name, aName, c++);
		if(!(bigB = fopen(name, "r")))
		{
	  		printf("Error opening %s for writing. Program terminated.",  aName);
	  		exit(1);
		}

		if(mpz_init_set_str (A, fgets(aArr, LENGTH, bigA), 10) == -1)
		{
			printf("Error initializing BigA from File");
	  		exit(1);
		}

		if(mpz_init_set_str (B, fgets(bArr, LENGTH, bigB), 10) == -1)
		{
			printf("Error initializing BigB from File");
	  		exit(1);
		}

		mpz_init(C);
		mpz_gcd (C, A, B);
	 	gmp_printf ("GCD %d calculated: %Zd\n", c/2, C);
	}
}

