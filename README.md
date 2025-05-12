# Finite Element Solver for 2D Heat Problem

This project is a Java + Processing implementation of a finite element solver for the 2D heat equation, defined over an upside-down T-shaped domain.

## Overview

The solver includes a set of custom classes to represent a finite element mesh composed of:

- **Nodes**
- **Edges** (used for efficient boundary condition integration)
- **Triangles**

The mesh is generated based on the input geometric parameters of the T-shaped profile. A `Scene` class encapsulates the full problem description and handles mesh creation, matrix assembly, and solution rendering.

The system matrix is assembled using the standard Galerkin finite element method and solved using the [Jama 1.0.3](http://math.nist.gov/javanumerics/jama/) linear algebra library. The final solution is visualized with Processing.

## Features

- Automatic mesh generation for a parameterized T-profile
- Edge-based boundary integration for flexibility
- Solution rendering with Processing
- Adjustable mesh resolution

## Problem Formulation

![Problem Definition](https://github.com/user-attachments/assets/e9d4995a-1503-48ea-94b3-dfc793190605)  
![Boundary Conditions](https://github.com/user-attachments/assets/8f9a10c4-9041-4951-a284-08c47466a18e)

## Software Architecture (UML)

![UML Diagram](https://github.com/user-attachments/assets/90d97576-9710-41f7-b68e-276b591abcd9)

## Results

**Solution with 136 Finite Elements**  
![136 Elements](https://github.com/user-attachments/assets/fdd1bcdc-4bd5-48f9-8186-2b833b6f8c6a)

**Solution with 544 Finite Elements**  
![544 Elements](https://github.com/user-attachments/assets/db3a3b6d-9b5b-424d-adf2-5e95a3208943)
