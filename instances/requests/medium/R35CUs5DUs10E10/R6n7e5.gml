graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 8
    prb 5
    x 23
    y 107
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 8
    prb 4
    x 68
    y 51
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 3
    prb 3
    x 18
    y 110
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 9
    prb 3
    x 29
    y 52
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 5
    prb 5
    x 93
    y 21
  ]
  edge [
    source 0
    target 6
    bandwith 925
    delay 182
  ]
  edge [
    source 0
    target 5
    bandwith 498
    delay 135
  ]
  edge [
    source 1
    target 3
    bandwith 381
    delay 500
  ]
  edge [
    source 1
    target 4
    bandwith 546
    delay 380
  ]
  edge [
    source 1
    target 2
    bandwith 651
    delay 368
  ]
]
