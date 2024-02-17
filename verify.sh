#!/bin/bash
compressed_file=/Users/ryanmyang/Documents/School/CS\ 131/JavaHW/Pigzj.gz
pigz -d <"${compressed_file}" >decomp
# pigz -d <"${compressed_file}" | cmp - ./modules