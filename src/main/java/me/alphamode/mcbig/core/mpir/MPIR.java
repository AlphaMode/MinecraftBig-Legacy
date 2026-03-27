package me.alphamode.mcbig.core.mpir;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class MPIR {
    private static final Linker LINKER = Linker.nativeLinker();
    private static final SymbolLookup MPIR;

    static {
        MPIR = SymbolLookup.libraryLookup("/usr/local/lib/libmpir.so", Arena.global());
    }

    public static MethodHandle lookup(String name, FunctionDescriptor descriptor) {
        return LINKER.downcallHandle(
                MPIR.find(name).orElseThrow(() ->
                        new UnsatisfiedLinkError("Symbol not found: " + name)),
                descriptor
        );
    }

    // Big Int stuff
    public static final MemoryLayout MPZ_T = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("_mp_alloc"),
            ValueLayout.JAVA_INT.withName("_mp_size"),
            ValueLayout.ADDRESS.withName("_mp_d")
    );

    public static final MethodHandle MPZ_CLEAR = lookup("__gmpz_clear",
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

    public static final MethodHandle MPZ_SET_F = lookup("__gmpz_set_f",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,   // mpz_t * destination
                    ValueLayout.ADDRESS    // mpf_t * source
            ));

    public static final MethodHandle MPZ_SET_STR = lookup("__gmpz_set_str",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,   // mpz_t *
                    ValueLayout.ADDRESS,   // const char *
                    ValueLayout.JAVA_INT   // base
            ));

    public static final MethodHandle MPZ_SET_STR_D = lookup("__gmpz_set_str",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,   // mpz_t *
                    ValueLayout.ADDRESS,   // const char *
                    ValueLayout.JAVA_INT   // base
            ));

    public static final MethodHandle MPZ_GET_SI = lookup("__gmpz_get_si",
            FunctionDescriptor.of(ValueLayout.JAVA_LONG,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPZ_GET_D = lookup("__gmpz_get_d",
            FunctionDescriptor.of(ValueLayout.JAVA_DOUBLE,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPZ_GET_STR = lookup("__gmpz_get_str",
            FunctionDescriptor.of(ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,   // char * (output buffer, or NULL)
                    ValueLayout.JAVA_INT,  // base
                    ValueLayout.ADDRESS    // mpz_t *
            ));

    public static final MethodHandle MPZ_ADD = lookup("__gmpz_add",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,   // result
                    ValueLayout.ADDRESS,   // op1
                    ValueLayout.ADDRESS    // op2
            ));

    public static final MethodHandle MPZ_SUB = lookup("__gmpz_sub",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPZ_MUL = lookup("__gmpz_mul",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPZ_CMP = lookup("__gmpz_cmp",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPZ_CMP_SI = lookup("__gmpz_cmp_si",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG
            ));

    public static final MethodHandle MPZ_POW_UI = lookup("__gmpz_pow_ui",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG  // unsigned long exponent
            ));

    public static final MethodHandle MPZ_POW_D = lookup("__gmpz_cmp_d",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG  // unsigned long exponent
            ));

    public static final MethodHandle MPZ_INIT = lookup("__gmpz_init",
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

    // From String
    public static final MethodHandle MPZ_INIT_SET_STR = lookup("__gmpz_init_set_str",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS, // mpz_t *
                    ValueLayout.ADDRESS, // const char *
                    ValueLayout.JAVA_INT // base
            ));

    // From Signed ints
    public static final MethodHandle MPZ_INIT_SET_SI = lookup("__gmpz_init_set_si",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,  // mpz_t *
                    ValueLayout.ADDRESS,  // const char *
                    ValueLayout.JAVA_LONG // signed long
            ));

    // Bit operations
    public static final MethodHandle MPZ_AND = lookup("__gmpz_and",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    // Big Dec stuff

    public static final MemoryLayout MPF_T_LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("_mp_prec"),
            ValueLayout.JAVA_INT.withName("_mp_size"),
            ValueLayout.JAVA_INT.withName("_mp_exp"),
            MemoryLayout.paddingLayout(4),             // alignment padding
            ValueLayout.ADDRESS.withName("_mp_d")
    );

    public static final MethodHandle MPF_INIT = lookup("__gmpf_init",
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

    public static final MethodHandle MPF_INIT2 = lookup("__gmpf_init2",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG   // precision in bits
            ));

    public static final MethodHandle MPF_SET_D = lookup("__gmpf_set_d",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_DOUBLE
            ));

    public static final MethodHandle MPF_CLEAR = lookup("__gmpf_clear",
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

    public static final MethodHandle MPF_SET_STR = lookup("__gmpf_set_str",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,    // mpf_t *
                    ValueLayout.ADDRESS,    // const char *
                    ValueLayout.JAVA_INT    // base
            ));

    public static final MethodHandle MPF_GET_STR = lookup("__gmpf_get_str",
            FunctionDescriptor.of(ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,    // char * output buffer (or NULL)
                    ValueLayout.ADDRESS,    // mp_exp_t * exponent output
                    ValueLayout.JAVA_INT,   // base
                    ValueLayout.JAVA_INT,   // num digits (0 = all)
                    ValueLayout.ADDRESS     // mpf_t *
            ));

    public static final MethodHandle MPF_ADD = lookup("__gmpf_add",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPF_SUB = lookup("__gmpf_sub",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPF_MUL = lookup("__gmpf_mul",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPF_DIV = lookup("__gmpf_div",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPF_CMP = lookup("__gmpf_cmp",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPF_CMP_D = lookup("__gmpf_cmp_d",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_DOUBLE
            ));

    public static final MethodHandle MPF_CMP_SI = lookup("__gmpf_cmp_si",
            FunctionDescriptor.of(ValueLayout.JAVA_INT,
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG
            ));

    public static final MethodHandle MPF_GET_D = lookup("__gmpf_get_d",
            FunctionDescriptor.of(
                    ValueLayout.JAVA_DOUBLE,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPF_GET_PREC = lookup("__gmpf_get_prec",
            FunctionDescriptor.of(ValueLayout.JAVA_LONG,
                    ValueLayout.ADDRESS
            ));

    public static final MethodHandle MPF_SET_PREC = lookup("__gmpf_set_prec",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,
                    ValueLayout.JAVA_LONG
            ));

    public static final MethodHandle MPF_SET_Z = lookup("__gmpf_set_z",
            FunctionDescriptor.ofVoid(
                    ValueLayout.ADDRESS,   // mpf_t * destination
                    ValueLayout.ADDRESS    // mpz_t * source
            ));

}
