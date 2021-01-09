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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 4
    prb 3
    x 31
    y 20
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 10
    prb 5
    x 27
    y 50
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 9
    prb 5
    x 15
    y 97
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 10
    prb 3
    x 99
    y 69
  ]
  edge [
    source 0
    target 5
    bandwith 191
    delay 336
  ]
  edge [
    source 1
    target 4
    bandwith 669
    delay 206
  ]
  edge [
    source 2
    target 3
    bandwith 973
    delay 130
  ]
  edge [
    source 2
    target 6
    bandwith 528
    delay 307
  ]
]
