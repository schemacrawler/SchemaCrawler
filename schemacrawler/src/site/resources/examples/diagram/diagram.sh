rm -f database-diagram.pdf
../_schemacrawler/sc.sh -database=schemacrawler -user=sa -password= -infolevel=maximum -command=diagram -outputformat=pdf -outputfile=database-diagram.pdf $*
echo Database diagram is in database-diagram.pdf