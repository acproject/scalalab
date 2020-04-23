/*
 * Copyright (c) 2009-2017, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ejml2.simple.ops;

import org.ejml2.data.FMatrixRMaj;
import org.ejml2.data.Matrix;
import org.ejml2.dense.row.CommonOps_FDRM;
import org.ejml2.dense.row.MatrixFeatures_FDRM;
import org.ejml2.dense.row.NormOps_FDRM;
import org.ejml2.dense.row.mult.VectorVectorMult_FDRM;
import org.ejml2.ops.MatrixIO;
import org.ejml2.simple.SimpleOperations;

import java.io.PrintStream;

/**
 * @author Peter Abeles
 */
public class SimpleOperations_FDRM implements SimpleOperations<FMatrixRMaj> {
    @Override
    public void transpose(FMatrixRMaj input, FMatrixRMaj output) {
        CommonOps_FDRM.transpose(input,output);
    }

    @Override
    public void mult(FMatrixRMaj A, FMatrixRMaj B, FMatrixRMaj output) {
        CommonOps_FDRM.mult(A,B,output);
    }

    @Override
    public void kron(FMatrixRMaj A, FMatrixRMaj B, FMatrixRMaj output) {
        CommonOps_FDRM.kron(A,B,output);
    }

    @Override
    public void plus(FMatrixRMaj A, FMatrixRMaj B, FMatrixRMaj output) {
        CommonOps_FDRM.add(A,B,output);
    }

    @Override
    public void minus(FMatrixRMaj A, FMatrixRMaj B, FMatrixRMaj output) {
        CommonOps_FDRM.subtract(A,B,output);
    }

    @Override
    public void minus(FMatrixRMaj A, /**/double b, FMatrixRMaj output) {
        CommonOps_FDRM.subtract(A, (float)b, output);
    }

    @Override
    public void plus(FMatrixRMaj A, /**/double b, FMatrixRMaj output) {
        CommonOps_FDRM.add(A, (float)b, output);
    }

    @Override
    public void plus(FMatrixRMaj A, /**/double beta, FMatrixRMaj b, FMatrixRMaj output) {
        CommonOps_FDRM.add(A, (float)beta, b, output);
    }

    @Override
    public /**/double dot(FMatrixRMaj A, FMatrixRMaj v) {
        return VectorVectorMult_FDRM.innerProd(A, v);
    }

    @Override
    public void scale(FMatrixRMaj A, /**/double val, FMatrixRMaj output) {
        CommonOps_FDRM.scale( (float)val, A,output);
    }

    @Override
    public void divide(FMatrixRMaj A, /**/double val, FMatrixRMaj output) {
        CommonOps_FDRM.divide( A, (float)val,output);
    }

    @Override
    public boolean invert(FMatrixRMaj A, FMatrixRMaj output) {
        return CommonOps_FDRM.invert(A,output);
    }

    @Override
    public void pseudoInverse(FMatrixRMaj A, FMatrixRMaj output) {
        CommonOps_FDRM.pinv(A,output);
    }

    @Override
    public boolean solve(FMatrixRMaj A, FMatrixRMaj X, FMatrixRMaj B) {
        return CommonOps_FDRM.solve(A,B,X);
    }

    @Override
    public void set(FMatrixRMaj A, /**/double val) {
        CommonOps_FDRM.fill(A, (float)val );
    }

    @Override
    public void zero(FMatrixRMaj A) {
        A.zero();
    }

    @Override
    public /**/double normF(FMatrixRMaj A) {
        return NormOps_FDRM.normF(A);
    }

    @Override
    public /**/double conditionP2(FMatrixRMaj A) {
        return NormOps_FDRM.conditionP2(A);
    }

    @Override
    public /**/double determinant(FMatrixRMaj A) {
        return CommonOps_FDRM.det(A);
    }

    @Override
    public /**/double trace(FMatrixRMaj A) {
        return CommonOps_FDRM.trace(A);
    }

    @Override
    public void setRow(FMatrixRMaj A, int row, int startColumn, /**/double... values) {
        for (int i = 0; i < values.length; i++) {
            A.set(row, startColumn + i, (float)values[i]);
        }
    }

    @Override
    public void setColumn(FMatrixRMaj A, int column, int startRow,  /**/double... values) {
        for (int i = 0; i < values.length; i++) {
            A.set(startRow + i, column, (float)values[i]);
        }
    }

    @Override
    public void extract(FMatrixRMaj src, int srcY0, int srcY1, int srcX0, int srcX1, FMatrixRMaj dst, int dstY0, int dstX0) {
        CommonOps_FDRM.extract(src,srcY0,srcY1,srcX0,srcX1,dst,dstY0,dstX0);
    }

    @Override
    public boolean hasUncountable(FMatrixRMaj M) {
        return MatrixFeatures_FDRM.hasUncountable(M);
    }

    @Override
    public void changeSign(FMatrixRMaj a) {
        CommonOps_FDRM.changeSign(a);
    }

    @Override
    public /**/double elementMaxAbs(FMatrixRMaj A) {
        return CommonOps_FDRM.elementMaxAbs(A);
    }

    @Override
    public /**/double elementSum(FMatrixRMaj A) {
        return CommonOps_FDRM.elementSum(A);
    }

    @Override
    public void elementMult(FMatrixRMaj A, FMatrixRMaj B, FMatrixRMaj output) {
        CommonOps_FDRM.elementMult(A,B,output);
    }

    @Override
    public void elementDiv(FMatrixRMaj A, FMatrixRMaj B, FMatrixRMaj output) {
        CommonOps_FDRM.elementDiv(A,B,output);
    }

    @Override
    public void elementPower(FMatrixRMaj A, FMatrixRMaj B, FMatrixRMaj output) {
        CommonOps_FDRM.elementPower(A,B,output);

    }

    @Override
    public void elementPower(FMatrixRMaj A, /**/double b, FMatrixRMaj output) {
        CommonOps_FDRM.elementPower(A, (float)b, output);
    }

    @Override
    public void elementExp(FMatrixRMaj A, FMatrixRMaj output) {
        CommonOps_FDRM.elementExp(A,output);
    }

    @Override
    public void elementLog(FMatrixRMaj A, FMatrixRMaj output) {
        CommonOps_FDRM.elementLog(A,output);
    }

    @Override
    public void print(PrintStream out, Matrix mat) {
        MatrixIO.print(out, (FMatrixRMaj)mat);
    }
}
